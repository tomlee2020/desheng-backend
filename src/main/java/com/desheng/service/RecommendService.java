package com.desheng.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.ServiceImpl;
import com.desheng.mapper.SearchHistoryMapper;
import com.desheng.mapper.SeedMapper;
import com.desheng.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务
 * 实现"猜你喜欢"的推荐逻辑
 * 包括基于内容的推荐、用户画像匹配、热门品种推荐等
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService extends ServiceImpl<SeedMapper, Seed> {

    private final SeedMapper seedMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final SemanticSearchService semanticSearchService;
    private final EmbeddingClient embeddingClient;

    /**
     * 获取个性化推荐
     * 综合使用多种推荐策略
     *
     * @param userId 用户 ID
     * @param limit 推荐数量
     * @return 推荐结果列表
     */
    public List<RecommendationDto> getPersonalizedRecommendations(String userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 6;
        }
        if (limit > 20) {
            limit = 20;
        }

        try {
            // 1. 构建用户画像
            UserProfile userProfile = buildUserProfile(userId);

            // 2. 获取多个推荐来源的结果
            List<RecommendationDto> contentBasedRecs = getContentBasedRecommendations(userProfile, limit / 2);
            List<RecommendationDto> userProfileRecs = getUserProfileBasedRecommendations(userProfile, limit / 2);
            List<RecommendationDto> trendingRecs = getTrendingRecommendations(limit / 4);

            // 3. 合并、去重、排序
            List<RecommendationDto> allRecommendations = new ArrayList<>();
            allRecommendations.addAll(contentBasedRecs);
            allRecommendations.addAll(userProfileRecs);
            allRecommendations.addAll(trendingRecs);

            // 按相似度分数排序，去重
            return allRecommendations.stream()
                    .collect(Collectors.toMap(
                            RecommendationDto::getId,
                            r -> r,
                            (r1, r2) -> r1.getScore() > r2.getScore() ? r1 : r2
                    ))
                    .values()
                    .stream()
                    .sorted((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()))
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error generating personalized recommendations for user: {}", userId, e);
            // 降级方案：返回热门品种
            return getTrendingRecommendations(limit);
        }
    }

    /**
     * 构建用户画像
     * 基于用户的搜索历史分析其偏好
     */
    private UserProfile buildUserProfile(String userId) {
        // 获取用户的搜索历史
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
               .orderByDesc(SearchHistory::getCreatedAt)
               .last("LIMIT 50"); // 最多取最近 50 条搜索记录

        List<SearchHistory> searchHistories = searchHistoryMapper.selectList(wrapper);

        // 提取搜索关键词
        List<String> keywords = searchHistories.stream()
                .map(SearchHistory::getQuery)
                .distinct()
                .collect(Collectors.toList());

        // 分析作物类型和地区偏好
        Map<String, Integer> cropTypeFreq = new HashMap<>();
        Map<String, Integer> regionFreq = new HashMap<>();

        for (String keyword : keywords) {
            // 简单的关键词分析（实际应用中可以使用 NLP）
            if (keyword.contains("水稻")) cropTypeFreq.merge("水稻", 1, Integer::sum);
            if (keyword.contains("玉米")) cropTypeFreq.merge("玉米", 1, Integer::sum);
            if (keyword.contains("小麦")) cropTypeFreq.merge("小麦", 1, Integer::sum);
            if (keyword.contains("大豆")) cropTypeFreq.merge("大豆", 1, Integer::sum);

            if (keyword.contains("华北")) regionFreq.merge("华北", 1, Integer::sum);
            if (keyword.contains("华东")) regionFreq.merge("华东", 1, Integer::sum);
            if (keyword.contains("华中")) regionFreq.merge("华中", 1, Integer::sum);
            if (keyword.contains("华南")) regionFreq.merge("华南", 1, Integer::sum);
        }

        // 获取最偏好的作物类型和地区
        String preferredCropType = cropTypeFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        String preferredRegion = regionFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return UserProfile.builder()
                .userId(userId)
                .searchKeywords(keywords)
                .cropTypeFrequency(cropTypeFreq)
                .regionFrequency(regionFreq)
                .preferredCropType(preferredCropType)
                .preferredRegion(preferredRegion)
                .totalSearchCount(searchHistories.size())
                .lastSearchTime(System.currentTimeMillis())
                .build();
    }

    /**
     * 基于内容的推荐
     * 根据用户最近搜索的关键词，找到语义相似的种子
     */
    private List<RecommendationDto> getContentBasedRecommendations(UserProfile userProfile, Integer limit) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        if (userProfile.getSearchKeywords().isEmpty()) {
            return recommendations;
        }

        try {
            // 取用户最近搜索的 3 个关键词
            List<String> recentKeywords = userProfile.getSearchKeywords().stream()
                    .limit(3)
                    .collect(Collectors.toList());

            // 对每个关键词进行语义搜索
            for (String keyword : recentKeywords) {
                try {
                    // 使用 Spring AI 进行语义搜索
                    List<Seed> semanticResults = semanticSearchService.searchByQuery(keyword, limit);

                    for (Seed seed : semanticResults) {
                        RecommendationDto rec = convertToRecommendationDto(seed);
                        rec.setReason("基于您搜索过的\"" + keyword + "\"推荐");
                        rec.setRecommendationType("content-based");
                        rec.setScore(0.85); // 基于内容的推荐分数
                        recommendations.add(rec);
                    }
                } catch (Exception e) {
                    log.warn("Semantic search failed for keyword: {}", keyword, e);
                }
            }

        } catch (Exception e) {
            log.error("Error in content-based recommendations", e);
        }

        return recommendations.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 基于用户画像的推荐
     * 根据用户的作物类型和地区偏好推荐
     */
    private List<RecommendationDto> getUserProfileBasedRecommendations(UserProfile userProfile, Integer limit) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        try {
            LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();

            // 优先匹配用户偏好的作物类型
            if (userProfile.getPreferredCropType() != null) {
                wrapper.eq(Seed::getCropType, userProfile.getPreferredCropType());
            }

            // 其次匹配用户偏好的地区
            if (userProfile.getPreferredRegion() != null) {
                wrapper.eq(Seed::getApprovalRegion, userProfile.getPreferredRegion());
            }

            // 按审定年份倒序（推荐最新的品种）
            wrapper.orderByDesc(Seed::getApprovalYear);
            wrapper.last("LIMIT " + limit);

            List<Seed> seeds = seedMapper.selectList(wrapper);

            for (Seed seed : seeds) {
                RecommendationDto rec = convertToRecommendationDto(seed);
                rec.setReason("基于您对" + userProfile.getPreferredCropType() + "的关注推荐");
                rec.setRecommendationType("user-profile");
                rec.setScore(0.80);
                recommendations.add(rec);
            }

        } catch (Exception e) {
            log.error("Error in user profile-based recommendations", e);
        }

        return recommendations;
    }

    /**
     * 热门品种推荐（趋势推荐）
     * 基于搜索热度和最新审定的品种
     */
    private List<RecommendationDto> getTrendingRecommendations(Integer limit) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        try {
            // 获取最近 1 年审定的品种
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Integer lastYear = currentYear - 1;

            LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Seed::getApprovalYear, lastYear)
                   .orderByDesc(Seed::getApprovalYear)
                   .orderByDesc(Seed::getId)
                   .last("LIMIT " + limit);

            List<Seed> trendingSeeds = seedMapper.selectList(wrapper);

            for (Seed seed : trendingSeeds) {
                RecommendationDto rec = convertToRecommendationDto(seed);
                rec.setReason("最新审定的优质品种");
                rec.setRecommendationType("trending");
                rec.setScore(0.75);
                recommendations.add(rec);
            }

        } catch (Exception e) {
            log.error("Error in trending recommendations", e);
        }

        return recommendations;
    }

    /**
     * 将 Seed 实体转换为 RecommendationDto
     */
    private RecommendationDto convertToRecommendationDto(Seed seed) {
        return RecommendationDto.builder()
                .id(seed.getId())
                .varietyName(seed.getVarietyName())
                .cropType(seed.getCropType())
                .approvalNumber(seed.getApprovalNumber())
                .approvalYear(seed.getApprovalYear())
                .approvalRegion(seed.getApprovalRegion())
                .company(seed.getCompany())
                .description(seed.getDescription())
                .characteristics(seed.getCharacteristics())
                .score(0.0)
                .build();
    }

    /**
     * 获取冷启动推荐（新用户）
     * 直接返回热门品种
     */
    public List<RecommendationDto> getColdStartRecommendations(Integer limit) {
        return getTrendingRecommendations(limit);
    }

    /**
     * 获取相似品种推荐
     * 基于指定品种 ID 推荐相似的品种
     */
    public List<RecommendationDto> getSimilarSeedsRecommendations(Long seedId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }

        try {
            // 获取指定品种
            Seed targetSeed = seedMapper.selectById(seedId);
            if (targetSeed == null) {
                return new ArrayList<>();
            }

            // 构建查询条件：相同作物类型和地区
            LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Seed::getCropType, targetSeed.getCropType())
                   .eq(Seed::getApprovalRegion, targetSeed.getApprovalRegion())
                   .ne(Seed::getId, seedId) // 排除自己
                   .orderByDesc(Seed::getApprovalYear)
                   .last("LIMIT " + limit);

            List<Seed> similarSeeds = seedMapper.selectList(wrapper);

            return similarSeeds.stream()
                    .map(seed -> {
                        RecommendationDto rec = convertToRecommendationDto(seed);
                        rec.setReason("与\"" + targetSeed.getVarietyName() + "\"相似的品种");
                        rec.setRecommendationType("similar");
                        rec.setScore(0.90);
                        return rec;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error in similar seeds recommendations", e);
            return new ArrayList<>();
        }
    }
}
