package com.desheng.service;

import com.desheng.mapper.SeedMapper;
import com.desheng.model.Seed;
import com.desheng.model.SeedDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 种子数据同步服务
 * 负责 MySQL 与 Elasticsearch 之间的数据同步
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeedSyncService {

    private final SeedMapper seedMapper;
    private final SeedSearchService seedSearchService;

    /**
     * 全量同步：将 MySQL 中的所有种子数据同步到 Elasticsearch
     * 注意：这是一个重操作，建议在系统初始化或维护时期执行
     */
    public void syncAllSeeds() {
        log.info("Starting full sync from MySQL to Elasticsearch...");
        
        try {
            // 1. 清空 Elasticsearch 中的旧数据
            seedSearchService.deleteAllDocuments();
            log.info("Cleared all documents in Elasticsearch");
            
            // 2. 从 MySQL 查询所有种子
            List<Seed> seeds = seedMapper.selectList(null);
            log.info("Retrieved {} seeds from MySQL", seeds.size());
            
            // 3. 转换为 SeedDocument 并保存到 Elasticsearch
            List<SeedDocument> documents = seeds.stream()
                    .map(seedSearchService::convertToDocument)
                    .collect(Collectors.toList());
            
            seedSearchService.saveDocuments(documents);
            log.info("Successfully synced {} seeds to Elasticsearch", documents.size());
            
        } catch (Exception e) {
            log.error("Error during full sync", e);
            throw new RuntimeException("Failed to sync seeds to Elasticsearch", e);
        }
    }

    /**
     * 增量同步：同步单个种子到 Elasticsearch
     * 在新增或更新种子时调用
     */
    public void syncSingleSeed(Seed seed) {
        log.info("Syncing single seed to Elasticsearch: {}", seed.getId());
        
        try {
            SeedDocument document = seedSearchService.convertToDocument(seed);
            seedSearchService.saveDocument(document);
            log.info("Successfully synced seed {} to Elasticsearch", seed.getId());
        } catch (Exception e) {
            log.error("Error syncing single seed to Elasticsearch", e);
            // 不抛出异常，避免影响主业务流程
        }
    }

    /**
     * 删除同步：从 Elasticsearch 删除种子
     * 在删除种子时调用
     */
    public void deleteSyncedSeed(Long seedId) {
        log.info("Deleting seed from Elasticsearch: {}", seedId);
        
        try {
            seedSearchService.deleteDocument(seedId);
            log.info("Successfully deleted seed {} from Elasticsearch", seedId);
        } catch (Exception e) {
            log.error("Error deleting seed from Elasticsearch", e);
            // 不抛出异常，避免影响主业务流程
        }
    }
}
