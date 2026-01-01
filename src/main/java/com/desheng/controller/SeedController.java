package com.desheng.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.desheng.model.Seed;
import com.desheng.service.SeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeds")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SeedController {

    private final SeedService seedService;

    /**
     * GET /api/seeds
     * 获取所有种子（分页）
     */
    @GetMapping
    public ResponseEntity<IPage<Seed>> getAllSeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        log.info("GET /api/seeds - page: {}, pageSize: {}, sortBy: {}, sortOrder: {}", 
                 page, pageSize, sortBy, sortOrder);
        
        IPage<Seed> seeds = seedService.getAllSeeds(page, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(seeds);
    }

    /**
     * GET /api/seeds/search
     * 搜索种子（品种名、审定号、企业名）
     */
    @GetMapping("/search")
    public ResponseEntity<IPage<Seed>> searchSeeds(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        log.info("GET /api/seeds/search - keyword: {}, page: {}, pageSize: {}", keyword, page, pageSize);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        IPage<Seed> results = seedService.searchSeeds(keyword, page, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/seeds/filter
     * 高级筛选（多条件组合）
     */
    @GetMapping("/filter")
    public ResponseEntity<IPage<Seed>> filterSeeds(
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) String approvalRegion,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        log.info("GET /api/seeds/filter - cropType: {}, region: {}, years: {}-{}, company: {}", 
                 cropType, approvalRegion, startYear, endYear, company);
        
        IPage<Seed> results = seedService.advancedFilter(cropType, approvalRegion, startYear, endYear, 
                                                        company, page, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/seeds/{id}
     * 获取种子详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Seed> getSeedById(@PathVariable Long id) {
        log.info("GET /api/seeds/{} - Fetching seed details", id);
        
        Seed seed = seedService.getSeedById(id);
        if (seed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(seed);
    }

    /**
     * POST /api/seeds
     * 创建新种子
     */
    @PostMapping
    public ResponseEntity<Seed> createSeed(@RequestBody Seed seed) {
        log.info("POST /api/seeds - Creating new seed: {}", seed.getVarietyName());
        
        try {
            Seed createdSeed = seedService.createSeed(seed);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSeed);
        } catch (Exception e) {
            log.error("Error creating seed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/seeds/{id}
     * 更新种子
     */
    @PutMapping("/{id}")
    public ResponseEntity<Seed> updateSeed(@PathVariable Long id, @RequestBody Seed seedDetails) {
        log.info("PUT /api/seeds/{} - Updating seed", id);
        
        try {
            Seed updatedSeed = seedService.updateSeed(id, seedDetails);
            return ResponseEntity.ok(updatedSeed);
        } catch (RuntimeException e) {
            log.error("Seed not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating seed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/seeds/{id}
     * 删除种子
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeed(@PathVariable Long id) {
        log.info("DELETE /api/seeds/{} - Deleting seed", id);
        
        try {
            seedService.deleteSeed(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting seed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/seeds/crop-types
     * 获取所有可用的作物类型
     */
    @GetMapping("/crop-types")
    public ResponseEntity<String> getCropTypes() {
        log.info("GET /api/seeds/crop-types - Fetching available crop types");
        String cropTypes = "[\"水稻\", \"小麦\", \"玉米\", \"大豆\", \"棉花\"]";
        return ResponseEntity.ok(cropTypes);
    }
}
