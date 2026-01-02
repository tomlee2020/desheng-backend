package com.desheng.repository;

import com.desheng.model.SeedApprovalDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Elasticsearch Repository for SeedApprovalDocument
 */
@Repository
public interface SeedApprovalElasticsearchRepository extends ElasticsearchRepository<SeedApprovalDocument, String> {
    
    /**
     * 根据审定编号查找
     */
    Optional<SeedApprovalDocument> findByApprovalNumber(String approvalNumber);
}