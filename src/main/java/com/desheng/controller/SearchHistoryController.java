package com.desheng.controller;

import com.desheng.model.HotSearchDto;
import com.desheng.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 搜索历史控制器
 * 提供搜索历史记录、热搜榜单等接口
 */
@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    /**
     * POST /api/search-history/record
     * 记录搜索历史
     *
     * @param userId 用户 ID（如果为空则使用设备 ID）
     * @param query 搜索关键词
     * @param searchType 搜索类型（keyword, semantic, advanced）
     * @param resultCount 搜索结果数
     * @param ipAddress 用户 IP 地址
     * @param userAgent 用户代理信息
     * @return 成功或失败消息
     */
    @PostMapping("/record")
    public ResponseEntity<String> recordSearch(
            @RequestParam(required = false) String userId,
            @RequestParam String query,
            @RequestParam(defaultValue = "keyword") String searchType,
            @RequestParam(defaultValue = "0") Integer resultCount,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String userAgent) {

        // 如果未提供 userId，使用设备 ID（由前端生成）
        if (userId == null || userId.trim().isEmpty()) {
            userId = "anonymous_" + UUID.randomUUID().toString();
        }

        try {
            searchHistoryService.recordSearch(userId, query, searchType, resultCount, ipAddress, userAgent);
            return ResponseEntity.ok("Search history recorded successfully");
        } catch (Exception e) {
            log.error("Error recording search history", e);
            return ResponseEntity.badRequest().body("Failed to record search history");
        }
    }

    /**
     * GET /api/search-history/hot
     * 获取热搜榜单
     *
     * @param limit 返回结果数量（默认 10，最大 100）
     * @return 热搜词列表
     *
     * 示例：
     * GET /api/search-history/hot?limit=10
     */
    @GetMapping("/hot")
    public ResponseEntity<List<HotSearchDto>> getHotSearches(
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("Fetching hot searches with limit: {}", limit);

        try {
            List<HotSearchDto> hotSearches = searchHistoryService.getHotSearches(limit);
            return ResponseEntity.ok(hotSearches);
        } catch (Exception e) {
            log.error("Error fetching hot searches", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/search-history/user
     * 获取用户搜索历史
     *
     * @param userId 用户 ID
     * @param limit 返回结果数量（默认 10，最大 100）
     * @return 用户搜索历史列表
     *
     * 示例：
     * GET /api/search-history/user?userId=user123&limit=10
     */
    @GetMapping("/user")
    public ResponseEntity<List<String>> getUserSearchHistory(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Integer limit) {

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Fetching search history for user: {}", userId);

        try {
            List<String> history = searchHistoryService.getUserSearchHistory(userId, limit);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching user search history", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/search-history/user
     * 清除用户搜索历史
     *
     * @param userId 用户 ID
     * @return 成功或失败消息
     *
     * 示例：
     * DELETE /api/search-history/user?userId=user123
     */
    @DeleteMapping("/user")
    public ResponseEntity<String> clearUserSearchHistory(
            @RequestParam String userId) {

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        try {
            searchHistoryService.clearUserSearchHistory(userId);
            return ResponseEntity.ok("Search history cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing search history", e);
            return ResponseEntity.badRequest().body("Failed to clear search history");
        }
    }

    /**
     * DELETE /api/search-history/user/{query}
     * 删除指定的搜索历史记录
     *
     * @param userId 用户 ID
     * @param query 搜索关键词
     * @return 成功或失败消息
     *
     * 示例：
     * DELETE /api/search-history/user/water-rice?userId=user123
     */
    @DeleteMapping("/user/{query}")
    public ResponseEntity<String> deleteSearchHistory(
            @RequestParam String userId,
            @PathVariable String query) {

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        try {
            searchHistoryService.deleteSearchHistory(userId, query);
            return ResponseEntity.ok("Search history deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting search history", e);
            return ResponseEntity.badRequest().body("Failed to delete search history");
        }
    }
}
