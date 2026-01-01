package com.desheng.config;

import com.desheng.service.SemanticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 向量索引初始化器
 * 在应用启动完成后，自动初始化种子数据的向量索引
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class VectorIndexInitializer {

    private final SemanticSearchService semanticSearchService;

    /**
     * 应用启动完成后自动初始化向量索引
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeVectorIndex() {
        log.info("Application started, initializing vector index for semantic search...");
        
        try {
            // 自动初始化向量索引
            semanticSearchService.indexAllSeeds();
            log.info("Vector index initialized successfully");
        } catch (Exception e) {
            log.warn("Failed to initialize vector index automatically. You can manually call POST /api/semantic-search/index", e);
        }
    }
}
