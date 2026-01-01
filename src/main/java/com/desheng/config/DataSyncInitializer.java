package com.desheng.config;

import com.desheng.service.SeedSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动同步数据到 Elasticsearch
 * 在应用启动后自动执行全量同步
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSyncInitializer implements ApplicationRunner {

    private final SeedSyncService seedSyncService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting data sync initialization...");
        
        try {
            // 应用启动时自动同步所有数据到 Elasticsearch
            seedSyncService.syncAllSeeds();
            log.info("Data sync initialization completed successfully");
        } catch (Exception e) {
            log.error("Data sync initialization failed", e);
            // 不中断应用启动，但记录错误
        }
    }
}
