package com.desheng.service;

import com.desheng.model.Seed;
import com.desheng.model.SeedVector;
import com.desheng.repository.SeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 语义搜索服务
 * 使用 Spring AI 和 Redis 向量存储进行语义相似度搜索
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SemanticSearchService {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final SeedRepository seedRepository;

    /**
     * 将所有种子数据向量化并存储到 Redis 向量存储
     * 这是一个初始化操作，应在应用启动时执行
     */
    public void indexAllSeeds() {
        log.info("Starting to index all seeds for semantic search...");
        
        try {
            // 1. 从 MySQL 查询所有种子
            List<Seed> seeds = seedRepository.findAll();
            log.info("Retrieved {} seeds from MySQL", seeds.size());
            
            // 2. 转换为 Document 对象（Spring AI 的标准格式）
            List<Document> documents = seeds.stream()
                    .map(this::seedToDocument)
                    .collect(Collectors.toList());
            
            // 3. 添加到向量存储
            vectorStore.add(documents);
            log.info("Successfully indexed {} seeds to vector store", documents.size());
            
        } catch (Exception e) {
            log.error("Error indexing seeds", e);
            throw new RuntimeException("Failed to index seeds", e);
        }
    }

    /**
     * 语义搜索 - 根据查询文本找到最相似的种子
     * 
     * @param query 查询文本（可以是用户输入的任何描述）
     * @param topK 返回最相似的 K 个结果
     * @return 相似度最高的种子列表
     */
    public List<SeedVector> semanticSearch(String query, int topK) {
        log.info("Performing semantic search with query: {} (topK: {})", query, topK);
        
        try {
            // 1. 使用向量存储进行相似度搜索
            List<Document> results = vectorStore.similaritySearch(query, topK);
            
            // 2. 将结果转换为 SeedVector 对象
            List<SeedVector> vectors = results.stream()
                    .map(doc -> documentToSeedVector(doc))
                    .collect(Collectors.toList());
            
            log.info("Found {} similar seeds", vectors.size());
            return vectors;
            
        } catch (Exception e) {
            log.error("Error performing semantic search", e);
            throw new RuntimeException("Failed to perform semantic search", e);
        }
    }

    /**
     * 添加单个种子到向量存储
     */
    public void addSeedToIndex(Seed seed) {
        log.info("Adding seed {} to vector store", seed.getId());
        
        try {
            Document document = seedToDocument(seed);
            vectorStore.add(List.of(document));
            log.info("Successfully added seed {} to vector store", seed.getId());
        } catch (Exception e) {
            log.error("Error adding seed to vector store", e);
        }
    }

    /**
     * 删除种子从向量存储
     */
    public void removeSeedFromIndex(Long seedId) {
        log.info("Removing seed {} from vector store", seedId);
        
        try {
            // Spring AI 的 VectorStore 不直接支持删除，需要重新索引
            log.warn("Direct deletion not supported, consider re-indexing all seeds");
        } catch (Exception e) {
            log.error("Error removing seed from vector store", e);
        }
    }

    /**
     * 将 Seed 转换为 Spring AI Document
     */
    private Document seedToDocument(Seed seed) {
        String content = SeedVector.generateContent(seed);
        
        // 创建元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("seedId", seed.getId());
        metadata.put("varietyName", seed.getVarietyName());
        metadata.put("approvalNumber", seed.getApprovalNumber());
        metadata.put("cropType", seed.getCropType());
        metadata.put("company", seed.getCompany());
        metadata.put("approvalYear", seed.getApprovalYear());
        metadata.put("approvalRegion", seed.getApprovalRegion());
        
        return new Document(content, metadata);
    }

    /**
     * 将 Spring AI Document 转换为 SeedVector
     */
    private SeedVector documentToSeedVector(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        
        return SeedVector.builder()
                .seedId(((Number) metadata.get("seedId")).longValue())
                .varietyName((String) metadata.get("varietyName"))
                .approvalNumber((String) metadata.get("approvalNumber"))
                .cropType((String) metadata.get("cropType"))
                .company((String) metadata.get("company"))
                .content(document.getContent())
                .similarity(document.getScore())
                .build();
    }
}
