package com.desheng.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desheng.mapper.SeedMapper;
import com.desheng.model.Seed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Seed Service Layer
 * 使用 MyBatis-Plus 的 ServiceImpl 继承基本的 CRUD 操作
 * 集成 Elasticsearch 数据同步
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeedService extends ServiceImpl<SeedMapper, Seed> {

    private final SeedMapper seedMapper;
    private final SeedSyncService seedSyncService;

    /**
     * 获取所有种子（分页）
     */
    public IPage<Seed> getAllSeeds(int page, int pageSize, String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize); // MyBatis-Plus 分页从 1 开始

        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
        
        // 设置排序字段
        if ("asc".equalsIgnoreCase(sortOrder)) {
            if ("approvalYear".equals(sortBy)) {
                wrapper.orderByAsc(Seed::getApprovalYear);
            } else if ("company".equals(sortBy)) {
                wrapper.orderByAsc(Seed::getCompany);
            } else if ("varietyName".equals(sortBy)) {
                wrapper.orderByAsc(Seed::getVarietyName);
            } else if ("approvalNumber".equals(sortBy)) {
                wrapper.orderByAsc(Seed::getApprovalNumber);
            } else {
                wrapper.orderByAsc(Seed::getApprovalYear);
            }
        } else {
            if ("approvalYear".equals(sortBy)) {
                wrapper.orderByDesc(Seed::getApprovalYear);
            } else if ("company".equals(sortBy)) {
                wrapper.orderByDesc(Seed::getCompany);
            } else if ("varietyName".equals(sortBy)) {
                wrapper.orderByDesc(Seed::getVarietyName);
            } else if ("approvalNumber".equals(sortBy)) {
                wrapper.orderByDesc(Seed::getApprovalNumber);
            } else {
                wrapper.orderByDesc(Seed::getApprovalYear);
            }
        }

        log.info("Fetching all seeds - page: {}, pageSize: {}, sortBy: {}, sortOrder: {}", 
                 page, pageSize, sortBy, sortOrder);
        
        return this.page(pageRequest, wrapper);
    }

    /**
     * 搜索种子（品种名、审定号、企业名）
     */
    public IPage<Seed> searchSeeds(String keyword, int page, int pageSize, String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        
        log.info("Searching seeds with keyword: {}", keyword);
        
        return seedMapper.searchByKeyword(pageRequest, keyword);
    }

    /**
     * 按作物类型筛选
     */
    public IPage<Seed> filterByCropType(String cropType, int page, int pageSize, String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        
        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seed::getCropType, cropType);
        wrapper.orderByDesc(Seed::getApprovalYear);
        
        log.info("Filtering seeds by crop type: {}", cropType);
        
        return this.page(pageRequest, wrapper);
    }

    /**
     * 按审定地区筛选
     */
    public IPage<Seed> filterByApprovalRegion(String approvalRegion, int page, int pageSize, String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        
        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Seed::getApprovalRegion, approvalRegion);
        wrapper.orderByDesc(Seed::getApprovalYear);
        
        log.info("Filtering seeds by approval region: {}", approvalRegion);
        
        return this.page(pageRequest, wrapper);
    }

    /**
     * 按审定年份范围筛选
     */
    public IPage<Seed> filterByApprovalYearRange(Integer startYear, Integer endYear, int page, int pageSize, 
                                                  String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        
        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
        if (startYear != null) {
            wrapper.ge(Seed::getApprovalYear, startYear);
        }
        if (endYear != null) {
            wrapper.le(Seed::getApprovalYear, endYear);
        }
        wrapper.orderByDesc(Seed::getApprovalYear);
        
        log.info("Filtering seeds by approval year range: {} - {}", startYear, endYear);
        
        return this.page(pageRequest, wrapper);
    }

    /**
     * 按企业名称筛选
     */
    public IPage<Seed> filterByCompany(String company, int page, int pageSize, String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        
        LambdaQueryWrapper<Seed> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Seed::getCompany, company);
        wrapper.orderByDesc(Seed::getApprovalYear);
        
        log.info("Filtering seeds by company: {}", company);
        
        return this.page(pageRequest, wrapper);
    }

    /**
     * 高级筛选 - 多条件组合
     */
    public IPage<Seed> advancedFilter(String cropType, String approvalRegion, Integer startYear, Integer endYear,
                                      String company, int page, int pageSize, String sortBy, String sortOrder) {
        Page<Seed> pageRequest = new Page<>(page + 1, pageSize);
        
        log.info("Advanced filter - cropType: {}, region: {}, years: {}-{}, company: {}", 
                 cropType, approvalRegion, startYear, endYear, company);
        
        return seedMapper.findByMultipleFilters(pageRequest, cropType, approvalRegion, startYear, endYear, company);
    }

    /**
     * 根据 ID 获取种子
     */
    public Seed getSeedById(Long id) {
        log.info("Fetching seed with ID: {}", id);
        return this.getById(id);
    }

    /**
     * 创建新种子
     */
    public Seed createSeed(Seed seed) {
        log.info("Creating new seed: {}", seed.getVarietyName());
        this.save(seed);
        
        // 异步同步到 Elasticsearch
        try {
            seedSyncService.syncSingleSeed(seed);
        } catch (Exception e) {
            log.error("Failed to sync seed to Elasticsearch", e);
        }
        
        return seed;
    }

    /**
     * 更新种子
     */
    public Seed updateSeed(Long id, Seed seedDetails) {
        log.info("Updating seed with ID: {}", id);
        
        Seed seed = this.getById(id);
        if (seed == null) {
            throw new RuntimeException("Seed not found with ID: " + id);
        }

        // 只更新非空字段
        if (seedDetails.getVarietyName() != null) seed.setVarietyName(seedDetails.getVarietyName());
        if (seedDetails.getApprovalNumber() != null) seed.setApprovalNumber(seedDetails.getApprovalNumber());
        if (seedDetails.getApprovalYear() != null) seed.setApprovalYear(seedDetails.getApprovalYear());
        if (seedDetails.getApprovalRegion() != null) seed.setApprovalRegion(seedDetails.getApprovalRegion());
        if (seedDetails.getCropType() != null) seed.setCropType(seedDetails.getCropType());
        if (seedDetails.getCompany() != null) seed.setCompany(seedDetails.getCompany());
        if (seedDetails.getCompanyPhone() != null) seed.setCompanyPhone(seedDetails.getCompanyPhone());
        if (seedDetails.getCompanyAddress() != null) seed.setCompanyAddress(seedDetails.getCompanyAddress());
        if (seedDetails.getCharacteristics() != null) seed.setCharacteristics(seedDetails.getCharacteristics());
        if (seedDetails.getAdaptiveRegions() != null) seed.setAdaptiveRegions(seedDetails.getAdaptiveRegions());
        if (seedDetails.getDescription() != null) seed.setDescription(seedDetails.getDescription());

        this.updateById(seed);
        
        // 异步同步到 Elasticsearch
        try {
            seedSyncService.syncSingleSeed(seed);
        } catch (Exception e) {
            log.error("Failed to sync seed to Elasticsearch", e);
        }
        
        return seed;
    }

    /**
     * 删除种子
     */
    public void deleteSeed(Long id) {
        log.info("Deleting seed with ID: {}", id);
        this.removeById(id);
        
        // 异步同步到 Elasticsearch
        try {
            seedSyncService.deleteSyncedSeed(id);
        } catch (Exception e) {
            log.error("Failed to delete seed from Elasticsearch", e);
        }
    }
}
