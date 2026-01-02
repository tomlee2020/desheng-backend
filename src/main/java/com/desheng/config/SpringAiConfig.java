package com.desheng.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/**
 * Spring AI 配置类
 * 配置 OpenAI Embedding 模型和 Redis 向量存储
 */
@Configuration
public class SpringAiConfig {

    /**
     * 配置 Redis 向量存储
     * 使用 OpenAI 的 Embedding 模型将文本转换为向量
     */
    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.host")
    public VectorStore vectorStore(
            EmbeddingModel embeddingModel,
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port,
            @Value("${spring.data.redis.password:}") String password) {
        
        // 创建 JedisPooled 连接池
        JedisPooled jedisPooled;
        if (password != null && !password.isEmpty()) {
            jedisPooled = new JedisPooled(host, port, null, password);
        } else {
            jedisPooled = new JedisPooled(host, port);
        }
        
        // 返回 Redis 向量存储
        return org.springframework.ai.vectorstore.redis.RedisVectorStore.builder(jedisPooled, embeddingModel)
                .build();
    }
}
