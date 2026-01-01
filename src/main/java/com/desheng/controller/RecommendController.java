package com.desheng.controller;

import com.desheng.model.RecommendationDto;
import com.desheng.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 推荐 API 控制器
 * 提供"猜你喜欢"等推荐接口
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * GET /api/recommend/guess-like
     * 获取个性化推荐（猜你喜欢）
     *
     * @param userId 用户 ID（如果为空则使用设备 ID）
     * @param limit 推荐数量（默认 6，最多 20）
     * @return 推荐结果列表
     *
     * 示例：
     * GET /api/recommend/guess-like?userId=user123&limit=6
     */
    @GetMapping("/guess-like")
    public ResponseEntity<Map<String, Object>> getGuessLikeRecommendations(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "6") Integer limit) {

        // 如果未提供 userId，使用设备 ID
        if (userId == null || userId.trim().isEmpty()) {
            userId = "anonymous_" + UUID.randomUUID().toString();
        }

        log.info("Fetching guess-like recommendations for user: {}, limit: {}", userId, limit);

        try {
            List<RecommendationDto> recommendations = recommendService.getPersonalizedRecommendations(userId, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", recommendations);
            response.put("count", recommendations.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching recommendations", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "Failed to fetch recommendations");
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * GET /api/recommend/cold-start
     * 冷启动推荐（新用户）
     * 直接返回热门品种，无需用户历史数据
     *
     * @param limit 推荐数量（默认 6，最多 20）
     * @return 推荐结果列表
     *
     * 示例：
     * GET /api/recommend/cold-start?limit=6
     */
    @GetMapping("/cold-start")
    public ResponseEntity<Map<String, Object>> getColdStartRecommendations(
            @RequestParam(defaultValue = "6") Integer limit) {

        log.info("Fetching cold-start recommendations, limit: {}", limit);

        try {
            List<RecommendationDto> recommendations = recommendService.getColdStartRecommendations(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", recommendations);
            response.put("count", recommendations.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching cold-start recommendations", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "Failed to fetch recommendations");
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * GET /api/recommend/similar/{seedId}
     * 获取相似品种推荐
     * 基于指定品种 ID 推荐相似的品种
     *
     * @param seedId 种子 ID
     * @param limit 推荐数量（默认 5，最多 20）
     * @return 相似品种列表
     *
     * 示例：
     * GET /api/recommend/similar/123?limit=5
     */
    @GetMapping("/similar/{seedId}")
    public ResponseEntity<Map<String, Object>> getSimilarSeedsRecommendations(
            @PathVariable Long seedId,
            @RequestParam(defaultValue = "5") Integer limit) {

        log.info("Fetching similar seeds recommendations for seed: {}, limit: {}", seedId, limit);

        try {
            List<RecommendationDto> recommendations = recommendService.getSimilarSeedsRecommendations(seedId, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", recommendations);
            response.put("count", recommendations.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching similar seeds recommendations", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "Failed to fetch recommendations");
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * GET /api/recommend/trending
     * 获取趋势推荐（热门品种）
     * 基于最新审定和搜索热度
     *
     * @param limit 推荐数量（默认 10，最多 20）
     * @return 热门品种列表
     *
     * 示例：
     * GET /api/recommend/trending?limit=10
     */
    @GetMapping("/trending")
    public ResponseEntity<Map<String, Object>> getTrendingRecommendations(
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("Fetching trending recommendations, limit: {}", limit);

        try {
            // 使用冷启动推荐作为趋势推荐
            List<RecommendationDto> recommendations = recommendService.getColdStartRecommendations(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Success");
            response.put("data", recommendations);
            response.put("count", recommendations.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching trending recommendations", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "Failed to fetch recommendations");
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * POST /api/recommend/feedback
     * 推荐反馈接口
     * 用户可以反馈推荐的有用性，用于优化推荐算法
     *
     * @param userId 用户 ID
     * @param seedId 种子 ID
     * @param feedback 反馈类型（like, dislike, click）
     * @return 反馈结果
     *
     * 示例：
     * POST /api/recommend/feedback?userId=user123&seedId=101&feedback=like
     */
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> submitRecommendationFeedback(
            @RequestParam String userId,
            @RequestParam Long seedId,
            @RequestParam String feedback) {

        log.info("Received recommendation feedback - userId: {}, seedId: {}, feedback: {}", userId, seedId, feedback);

        try {
            // 这里可以将反馈信息保存到数据库，用于后续的推荐算法优化
            // 目前仅记录日志

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Feedback received successfully");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting recommendation feedback", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "Failed to submit feedback");
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
