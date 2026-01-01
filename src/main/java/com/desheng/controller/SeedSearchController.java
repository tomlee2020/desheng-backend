package com.desheng.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.desheng.model.SeedDocument;
import com.desheng.service.SeedSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 种子搜索控制器
 * 使用 Elasticsearch 进行高级搜索，支持拼音搜索、全文搜索等
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SeedSearchController {

    private final SeedSearchService seedSearchService;

    /**
     * GET /api/search/seeds
     * 搜索种子（支持品种名、拼音、审定号、企业名）
     * 
     * @param keyword 搜索关键词
     * @param page 页码（0-indexed）
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    @GetMapping("/seeds")
    public ResponseEntity<Page<SeedDocument>> searchSeeds(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("GET /api/search/seeds - keyword: {}, page: {}, pageSize: {}", keyword, page, pageSize);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<SeedDocument> results = seedSearchService.searchSeeds(keyword, page, pageSize);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/search/crop-type
     * 按作物类型搜索
     */
    @GetMapping("/crop-type")
    public ResponseEntity<Page<SeedDocument>> searchByCropType(
            @RequestParam String cropType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("GET /api/search/crop-type - cropType: {}", cropType);
        
        Page<SeedDocument> results = seedSearchService.searchByCropType(cropType, page, pageSize);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/search/region
     * 按审定地区搜索
     */
    @GetMapping("/region")
    public ResponseEntity<Page<SeedDocument>> searchByRegion(
            @RequestParam String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("GET /api/search/region - region: {}", region);
        
        Page<SeedDocument> results = seedSearchService.searchByApprovalRegion(region, page, pageSize);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/search/advanced
     * 高级搜索（多条件组合）
     * 
     * @param keyword 关键词
     * @param cropType 作物类型
     * @param approvalRegion 审定地区
     * @param startYear 审定年份开始
     * @param endYear 审定年份结束
     * @param company 企业名称
     * @param page 页码
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    @GetMapping("/advanced")
    public ResponseEntity<Page<SeedDocument>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) String approvalRegion,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("GET /api/search/advanced - keyword: {}, cropType: {}, region: {}, years: {}-{}, company: {}",
                 keyword, cropType, approvalRegion, startYear, endYear, company);
        
        Page<SeedDocument> results = seedSearchService.advancedSearch(keyword, cropType, approvalRegion,
                                                                      startYear, endYear, company, page, pageSize);
        return ResponseEntity.ok(results);
    }
}
