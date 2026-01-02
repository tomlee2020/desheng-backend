package com.desheng.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desheng.mapper.SearchHistoryMapper;
import com.desheng.model.HotSearchDto;
import com.desheng.model.SearchHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 搜索历史服务
 * 负责搜索历史的记录、查询和热搜统计
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchHistoryService extends ServiceImpl<SearchHistoryMapper, SearchHistory> {

    /**
     * 记录搜索历史
     */
    public void recordSearch(String userId, String query, String searchType, Integer resultCount, String ipAddress, String userAgent) {
        try {
            SearchHistory history = SearchHistory.builder()
                    .userId(userId)
                    .query(query)
                    .searchType(searchType)
                    .resultCount(resultCount)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
            this.save(history);
        } catch (Exception e) {
            log.warn("Failed to record search history: {}", query, e);
        }
    }

    /**
     * 获取热搜榜单
     * @param limit 返回结果数量（默认 10）
     * @return 热搜词列表，包含搜索次数和排名
     */
    public List<HotSearchDto> getHotSearches(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (limit > 100) {
            limit = 100;
        }

        List<HotSearchDto> hotSearches = this.baseMapper.getHotSearches(limit);
        
        // 为每个热搜添加排名
        IntStream.range(0, hotSearches.size())
                .forEach(i -> hotSearches.get(i).setRank(i + 1));

        return hotSearches;
    }

    /**
     * 获取用户搜索历史
     * @param userId 用户 ID
     * @param limit 返回结果数量（默认 10）
     * @return 搜索历史列表
     */
    public List<String> getUserSearchHistory(String userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (limit > 100) {
            limit = 100;
        }

        return this.baseMapper.getUserSearchHistory(userId, limit);
    }

    /**
     * 清除用户搜索历史
     */
    public void clearUserSearchHistory(String userId) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        this.remove(wrapper);
    }

    /**
     * 删除指定的搜索历史记录
     */
    public void deleteSearchHistory(String userId, String query) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
               .eq(SearchHistory::getQuery, query);
        this.remove(wrapper);
    }
}
