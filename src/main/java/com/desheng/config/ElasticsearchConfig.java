package com.desheng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch 配置类
 * 启用 Spring Data Elasticsearch Repository 扫描
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.desheng.repository")
public class ElasticsearchConfig {
    // Spring Boot 会自动配置 Elasticsearch 连接
    // 具体配置在 application.properties 中
}
