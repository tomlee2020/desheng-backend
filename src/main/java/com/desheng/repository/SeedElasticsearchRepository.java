package com.desheng.repository;

import com.desheng.model.SeedDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch Repository for SeedDocument
 * Spring Data Elasticsearch 自动提供基本的 CRUD 和搜索操作
 */
@Repository
public interface SeedElasticsearchRepository extends ElasticsearchRepository<SeedDocument, Long> {
    // Spring Data Elasticsearch 会自动提供基本的查询方法
    // 如需自定义查询，可以使用 @Query 注解
}
