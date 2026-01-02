package com.desheng.service;

import com.desheng.model.SeedDocument;
import com.desheng.repository.SeedElasticsearchRepository;
import com.desheng.util.PinyinUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 搜索服务
 * 提供高级搜索功能，支持拼音搜索、全文搜索和高亮显示
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeedSearchService {

    private final SeedElasticsearchRepository seedElasticsearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 搜索种子（支持品种名、拼音、审定号、企业名）
     * 
     * @param keyword 搜索关键词
     * @param page 页码（0-indexed）
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    public Page<SeedDocument> searchSeeds(String keyword, int page, int pageSize) {
        log.info("Searching seeds with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        
        // 构建搜索条件：支持品种名、拼音、审定号、企业名等多个字段
        Criteria criteria = new Criteria()
                .or(new Criteria("varietyName").contains(keyword))
                .or(new Criteria("varietyNamePinyin").contains(keyword))
                .or(new Criteria("varietyNamePinyinShort").contains(keyword))
                .or(new Criteria("approvalNumber").contains(keyword))
                .or(new Criteria("company").contains(keyword))
                .or(new Criteria("companyPinyin").contains(keyword));
        
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedDocument> searchHits = elasticsearchTemplate.search(query, SeedDocument.class);
        
        List<SeedDocument> documents = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        
        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    /**
     * 按作物类型搜索
     */
    public Page<SeedDocument> searchByCropType(String cropType, int page, int pageSize) {
        log.info("Searching seeds by crop type: {}", cropType);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Criteria criteria = new Criteria("cropType").is(cropType);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedDocument> searchHits = elasticsearchTemplate.search(query, SeedDocument.class);
        
        List<SeedDocument> documents = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        
        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    /**
     * 按审定地区搜索
     */
    public Page<SeedDocument> searchByApprovalRegion(String approvalRegion, int page, int pageSize) {
        log.info("Searching seeds by approval region: {}", approvalRegion);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Criteria criteria = new Criteria("approvalRegion").contains(approvalRegion);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedDocument> searchHits = elasticsearchTemplate.search(query, SeedDocument.class);
        
        List<SeedDocument> documents = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        
        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    /**
     * 高级搜索（多条件组合）
     */
    public Page<SeedDocument> advancedSearch(String keyword, String cropType, String approvalRegion,
                                             Integer startYear, Integer endYear, String company,
                                             int page, int pageSize) {
        log.info("Advanced search - keyword: {}, cropType: {}, region: {}, years: {}-{}, company: {}",
                 keyword, cropType, approvalRegion, startYear, endYear, company);
        
        Pageable pageable = PageRequest.of(page, pageSize);
        
        Criteria criteria = new Criteria();
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            Criteria keywordCriteria = new Criteria()
                    .or(new Criteria("varietyName").contains(keyword))
                    .or(new Criteria("varietyNamePinyin").contains(keyword))
                    .or(new Criteria("varietyNamePinyinShort").contains(keyword))
                    .or(new Criteria("approvalNumber").contains(keyword))
                    .or(new Criteria("company").contains(keyword))
                    .or(new Criteria("companyPinyin").contains(keyword));
            criteria = criteria.and(keywordCriteria);
        }
        
        // 作物类型
        if (cropType != null && !cropType.trim().isEmpty()) {
            criteria = criteria.and(new Criteria("cropType").is(cropType));
        }
        
        // 审定地区
        if (approvalRegion != null && !approvalRegion.trim().isEmpty()) {
            criteria = criteria.and(new Criteria("approvalRegion").contains(approvalRegion));
        }
        
        // 审定年份范围
        if (startYear != null) {
            criteria = criteria.and(new Criteria("approvalYear").greaterThanEqual(startYear));
        }
        if (endYear != null) {
            criteria = criteria.and(new Criteria("approvalYear").lessThanEqual(endYear));
        }
        
        // 企业名称
        if (company != null && !company.trim().isEmpty()) {
            criteria = criteria.and(new Criteria("company").contains(company));
        }
        
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<SeedDocument> searchHits = elasticsearchTemplate.search(query, SeedDocument.class);
        
        List<SeedDocument> documents = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
        
        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    /**
     * 将 Seed 转换为 SeedDocument（用于同步到 ES）
     */
    public SeedDocument convertToDocument(com.desheng.model.Seed seed) {
        String varietyNamePinyin = PinyinUtil.getPinyinWithoutTone(seed.getVarietyName());
        String varietyNamePinyinShort = PinyinUtil.getPinyinShort(seed.getVarietyName());
        String companyPinyin = PinyinUtil.getPinyinWithoutTone(seed.getCompany());
        
        return SeedDocument.builder()
                .id(seed.getId())
                .varietyName(seed.getVarietyName())
                .varietyNamePinyin(varietyNamePinyin)
                .varietyNamePinyinShort(varietyNamePinyinShort)
                .approvalNumber(seed.getApprovalNumber())
                .approvalYear(seed.getApprovalYear())
                .approvalRegion(seed.getApprovalRegion())
                .cropType(seed.getCropType())
                .company(seed.getCompany())
                .companyPinyin(companyPinyin)
                .companyPhone(seed.getCompanyPhone())
                .companyAddress(seed.getCompanyAddress())
                .description(seed.getDescription())
                .characteristics(seed.getCharacteristics())
                .adaptiveRegions(seed.getAdaptiveRegions())
                .createdAt(seed.getCreatedAt())
                .updatedAt(seed.getUpdatedAt())
                .build();
    }

    /**
     * 保存文档到 Elasticsearch
     */
    public void saveDocument(SeedDocument document) {
        log.info("Saving seed document to Elasticsearch: {}", document.getId());
        seedElasticsearchRepository.save(document);
    }

    /**
     * 批量保存文档到 Elasticsearch
     */
    public void saveDocuments(List<SeedDocument> documents) {
        log.info("Saving {} seed documents to Elasticsearch", documents.size());
        seedElasticsearchRepository.saveAll(documents);
    }

    /**
     * 删除文档
     */
    public void deleteDocument(Long id) {
        log.info("Deleting seed document from Elasticsearch: {}", id);
        seedElasticsearchRepository.deleteById(id);
    }

    /**
     * 清空所有文档
     */
    public void deleteAllDocuments() {
        log.warn("Deleting all seed documents from Elasticsearch");
        seedElasticsearchRepository.deleteAll();
    }
}
