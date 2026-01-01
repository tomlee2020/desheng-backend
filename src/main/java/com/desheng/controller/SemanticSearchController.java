package com.desheng.controller;

import com.desheng.model.SeedVector;
import com.desheng.service.SemanticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 语义搜索控制器
 * 提供基于 Spring AI 的语义搜索 API 端点
 */
@RestController
@RequestMapping("/api/semantic-search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;

    /**
     * POST /api/semantic-search/index
     * 初始化索引 - 将所有种子数据向量化并存储到向量存储
     * 注意：这是一个初始化操作，应在应用启动时执行一次
     */
    @PostMapping("/index")
    public ResponseEntity<String> indexSeeds() {
        log.info("Initializing seed index for semantic search...");
        
        try {
            semanticSearchService.indexAllSeeds();
            return ResponseEntity.ok("Seeds indexed successfully for semantic search");
        } catch (Exception e) {
            log.error("Error indexing seeds", e);
            return ResponseEntity.badRequest().body("Failed to index seeds: " + e.getMessage());
        }
    }

    /**
     * GET /api/semantic-search/search
     * 语义搜索 - 根据查询文本找到最相似的种子
     * 
     * @param query 查询文本（例如："抗倒伏的水稻品种"）
     * @param topK 返回最相似的 K 个结果（默认 10）
     * @return 相似度最高的种子列表
     * 
     * 示例：
     * GET /api/semantic-search/search?query=抗倒伏的水稻品种&topK=5
     * GET /api/semantic-search/search?query=高产量玉米&topK=10
     * GET /api/semantic-search/search?query=抗病性强的大豆&topK=5
     */
    @GetMapping("/search")
    public ResponseEntity<List<SeedVector>> semanticSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topK) {
        
        log.info("Semantic search - query: {}, topK: {}", query, topK);
        
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // 限制 topK 的最大值
        if (topK > 100) {
            topK = 100;
        }
        
        try {
            List<SeedVector> results = semanticSearchService.semanticSearch(query, topK);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error performing semantic search", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/semantic-search/example
     * 搜索示例 - 展示语义搜索的能力
     * 
     * @return 搜索示例列表
     */
    @GetMapping("/example")
    public ResponseEntity<String> getSearchExample() {
        String example = """
                语义搜索示例：
                
                1. 搜索抗倒伏的水稻品种：
                   /api/semantic-search/search?query=抗倒伏的水稻品种&topK=5
                
                2. 搜索高产量玉米：
                   /api/semantic-search/search?query=高产量玉米&topK=10
                
                3. 搜索适合华东地区的大豆：
                   /api/semantic-search/search?query=华东地区大豆&topK=5
                
                4. 搜索抗病性强的品种：
                   /api/semantic-search/search?query=抗病性强&topK=5
                
                5. 搜索生育期短的品种：
                   /api/semantic-search/search?query=生育期短&topK=5
                
                语义搜索的优势：
                - 即使用户输入的词不完全匹配，也能找到相关的种子
                - 支持自然语言查询，例如"我想要一个抗病性强的品种"
                - 能够理解用户的真实意图，而不仅仅是关键词匹配
                - 可以处理用户的拼写错误和同义词表达
                """;
        return ResponseEntity.ok(example);
    }
}
