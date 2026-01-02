package com.desheng.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch索引管理服务
 * 负责创建和配置ES索引，包括中文分词和拼音分词
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchIndexService {

    private final ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建种子审定详情索引
     */
    public void createSeedApprovalDetailsIndex() {
        try {
            IndexOperations indexOps = elasticsearchTemplate.indexOps(com.desheng.model.SeedApprovalDocument.class);
            
            // 如果索引已存在，先删除
            if (indexOps.exists()) {
                log.info("Index seed_approval_details already exists, deleting...");
                indexOps.delete();
            }

            // 创建索引设置
            Document settings = Document.create()
                    .append("number_of_shards", 1)
                    .append("number_of_replicas", 0)
                    .append("analysis", createAnalysisSettings());

            // 创建索引
            indexOps.create(settings);
            log.info("Created index: seed_approval_details");

            // 创建映射
            Document mapping = createSeedApprovalDetailsMapping();
            indexOps.putMapping(mapping);
            log.info("Created mapping for seed_approval_details");

        } catch (Exception e) {
            log.error("Failed to create seed approval details index", e);
            throw new RuntimeException("Failed to create ES index", e);
        }
    }

    /**
     * 创建分析器设置
     */
    private Map<String, Object> createAnalysisSettings() {
        Map<String, Object> analysis = new HashMap<>();
        
        // 分析器配置
        Map<String, Object> analyzers = new HashMap<>();
        analyzers.put("ik_max_word_analyzer", Map.of(
            "type", "custom",
            "tokenizer", "ik_max_word",
            "filter", List.of("lowercase")
        ));
        analyzers.put("ik_smart_analyzer", Map.of(
            "type", "custom", 
            "tokenizer", "ik_smart",
            "filter", List.of("lowercase")
        ));
        analyzers.put("pinyin_analyzer", Map.of(
            "type", "custom",
            "tokenizer", "pinyin_tokenizer",
            "filter", List.of("lowercase", "unique")
        ));
        analyzers.put("pinyin_first_letter_analyzer", Map.of(
            "type", "custom",
            "tokenizer", "pinyin_first_letter_tokenizer", 
            "filter", List.of("lowercase", "unique")
        ));
        analyzers.put("ik_pinyin_analyzer", Map.of(
            "type", "custom",
            "tokenizer", "ik_max_word",
            "filter", List.of("lowercase", "pinyin_filter", "unique")
        ));
        
        // 分词器配置
        Map<String, Object> tokenizers = new HashMap<>();
        tokenizers.put("pinyin_tokenizer", Map.of(
            "type", "pinyin",
            "keep_separate_first_letter", false,
            "keep_full_pinyin", true,
            "keep_original", true,
            "limit_first_letter_length", 16,
            "lowercase", true,
            "remove_duplicated_term", true
        ));
        tokenizers.put("pinyin_first_letter_tokenizer", Map.of(
            "type", "pinyin",
            "keep_separate_first_letter", true,
            "keep_full_pinyin", false,
            "keep_original", false,
            "limit_first_letter_length", 16,
            "lowercase", true,
            "remove_duplicated_term", true
        ));
        
        // 过滤器配置
        Map<String, Object> filters = new HashMap<>();
        filters.put("pinyin_filter", Map.of(
            "type", "pinyin",
            "keep_separate_first_letter", false,
            "keep_full_pinyin", true,
            "keep_original", true,
            "limit_first_letter_length", 16,
            "lowercase", true,
            "remove_duplicated_term", true
        ));
        
        analysis.put("analyzer", analyzers);
        analysis.put("tokenizer", tokenizers);
        analysis.put("filter", filters);
        
        return analysis;
    }

    /**
     * 创建种子审定详情映射
     */
    private Document createSeedApprovalDetailsMapping() {
        Map<String, Object> properties = new HashMap<>();
        
        properties.put("id", Map.of("type", "keyword"));
        properties.put("approvalNumber", Map.of(
            "type", "keyword",
            "fields", Map.of(
                "text", Map.of(
                    "type", "text",
                    "analyzer", "ik_max_word_analyzer",
                    "search_analyzer", "ik_smart_analyzer"
                )
            )
        ));
        properties.put("varietyName", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer",
            "fields", Map.of(
                "pinyin", Map.of(
                    "type", "text",
                    "analyzer", "pinyin_analyzer",
                    "search_analyzer", "pinyin_analyzer"
                ),
                "keyword", Map.of("type", "keyword")
            )
        ));
        properties.put("varietyNamePinyin", Map.of(
            "type", "text",
            "analyzer", "pinyin_analyzer",
            "search_analyzer", "pinyin_analyzer"
        ));
        properties.put("varietyNamePinyinShort", Map.of(
            "type", "text",
            "analyzer", "pinyin_first_letter_analyzer",
            "search_analyzer", "pinyin_first_letter_analyzer"
        ));
        properties.put("cropName", Map.of(
            "type", "keyword",
            "fields", Map.of(
                "text", Map.of(
                    "type", "text",
                    "analyzer", "ik_max_word_analyzer"
                )
            )
        ));
        properties.put("approvalYear", Map.of("type", "integer"));
        properties.put("applicant", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer",
            "fields", Map.of(
                "pinyin", Map.of(
                    "type", "text",
                    "analyzer", "pinyin_analyzer",
                    "search_analyzer", "pinyin_analyzer"
                ),
                "keyword", Map.of("type", "keyword")
            )
        ));
        properties.put("applicantPinyin", Map.of(
            "type", "text",
            "analyzer", "pinyin_analyzer",
            "search_analyzer", "pinyin_analyzer"
        ));
        properties.put("breeder", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer",
            "fields", Map.of(
                "pinyin", Map.of(
                    "type", "text",
                    "analyzer", "pinyin_analyzer",
                    "search_analyzer", "pinyin_analyzer"
                ),
                "keyword", Map.of("type", "keyword")
            )
        ));
        properties.put("breederPinyin", Map.of(
            "type", "text",
            "analyzer", "pinyin_analyzer",
            "search_analyzer", "pinyin_analyzer"
        ));
        properties.put("varietySource", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("isGMO", Map.of("type", "boolean"));
        properties.put("licenseInfo", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("varietyRights", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("approvalAuthority", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer",
            "fields", Map.of(
                "keyword", Map.of("type", "keyword")
            )
        ));
        properties.put("detailedDescription", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("growthPeriod", Map.of("type", "keyword"));
        properties.put("plantHeight", Map.of("type", "keyword"));
        properties.put("resistance", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("qualityTraits", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("yieldSummary", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("comparisonData", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("cultivationRequirements", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("cultivationTechniques", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("cultivationPrecautions", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("approvalOpinion", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("suitableRegions", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("plantingRestrictions", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("yieldData", Map.of(
            "type", "nested",
            "properties", Map.of(
                "year", Map.of("type", "integer"),
                "location", Map.of(
                    "type", "text",
                    "analyzer", "ik_max_word_analyzer",
                    "search_analyzer", "ik_smart_analyzer"
                ),
                "yieldValue", Map.of("type", "double"),
                "yieldUnit", Map.of("type", "keyword"),
                "comparisonVariety", Map.of(
                    "type", "text",
                    "analyzer", "ik_max_word_analyzer"
                ),
                "comparisonYield", Map.of("type", "double")
            )
        ));
        properties.put("createdAt", Map.of("type", "date"));
        properties.put("updatedAt", Map.of("type", "date"));
        properties.put("version", Map.of("type", "integer"));

        return Document.create().append("properties", properties);
    }

    /**
     * 创建种子索引（原有的）
     */
    public void createSeedIndex() {
        try {
            IndexOperations indexOps = elasticsearchTemplate.indexOps(com.desheng.model.SeedDocument.class);
            
            if (indexOps.exists()) {
                log.info("Index seeds already exists, deleting...");
                indexOps.delete();
            }

            Document settings = Document.create()
                    .append("number_of_shards", 1)
                    .append("number_of_replicas", 0)
                    .append("analysis", createAnalysisSettings());

            indexOps.create(settings);
            log.info("Created index: seeds");

            Document mapping = createSeedMapping();
            indexOps.putMapping(mapping);
            log.info("Created mapping for seeds");

        } catch (Exception e) {
            log.error("Failed to create seed index", e);
            throw new RuntimeException("Failed to create ES index", e);
        }
    }

    /**
     * 创建种子映射
     */
    private Document createSeedMapping() {
        Map<String, Object> properties = new HashMap<>();
        
        properties.put("id", Map.of("type", "long"));
        properties.put("varietyName", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer",
            "fields", Map.of(
                "pinyin", Map.of(
                    "type", "text",
                    "analyzer", "pinyin_analyzer",
                    "search_analyzer", "pinyin_analyzer"
                ),
                "keyword", Map.of("type", "keyword")
            )
        ));
        properties.put("varietyNamePinyin", Map.of(
            "type", "text",
            "analyzer", "pinyin_analyzer",
            "search_analyzer", "pinyin_analyzer"
        ));
        properties.put("varietyNamePinyinShort", Map.of(
            "type", "text",
            "analyzer", "pinyin_first_letter_analyzer",
            "search_analyzer", "pinyin_first_letter_analyzer"
        ));
        properties.put("approvalNumber", Map.of("type", "keyword"));
        properties.put("approvalYear", Map.of("type", "integer"));
        properties.put("approvalRegion", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("cropType", Map.of("type", "keyword"));
        properties.put("company", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer",
            "fields", Map.of(
                "pinyin", Map.of(
                    "type", "text",
                    "analyzer", "pinyin_analyzer",
                    "search_analyzer", "pinyin_analyzer"
                ),
                "keyword", Map.of("type", "keyword")
            )
        ));
        properties.put("companyPinyin", Map.of(
            "type", "text",
            "analyzer", "pinyin_analyzer",
            "search_analyzer", "pinyin_analyzer"
        ));
        properties.put("companyPhone", Map.of("type", "keyword"));
        properties.put("companyAddress", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("description", Map.of(
            "type", "text",
            "analyzer", "ik_max_word_analyzer",
            "search_analyzer", "ik_smart_analyzer"
        ));
        properties.put("characteristics", Map.of("type", "text"));
        properties.put("adaptiveRegions", Map.of("type", "text"));
        properties.put("createdAt", Map.of("type", "date"));
        properties.put("updatedAt", Map.of("type", "date"));

        return Document.create().append("properties", properties);
    }
}