package com.desheng.config;

import com.desheng.service.ElasticsearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch索引初始化器
 * 在应用启动时创建必要的ES索引和映射
 */
@Component
@Order(1) // 确保在数据初始化之前执行
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final ElasticsearchIndexService elasticsearchIndexService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing Elasticsearch indexes...");
        
        try {
            // 创建种子索引
            elasticsearchIndexService.createSeedIndex();
            
            // 创建种子审定详情索引
            elasticsearchIndexService.createSeedApprovalDetailsIndex();
            
            log.info("Successfully initialized all Elasticsearch indexes");
        } catch (Exception e) {
            log.error("Failed to initialize Elasticsearch indexes", e);
            // 不抛出异常，允许应用继续启动
        }
    }
}