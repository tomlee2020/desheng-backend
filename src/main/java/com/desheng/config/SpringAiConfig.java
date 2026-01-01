package com.desheng.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
    public VectorStore vectorStore(EmbeddingModel embeddingModel, RedisConnectionFactory connectionFactory) {
        // 获取 Redis 连接信息
        String host = connectionFactory.getConnection().getClientName();
        
        // 创建 JedisPooled 连接池
        JedisPooled jedisPooled = new JedisPooled(
            connectionFactory.getConnection().getHost(),
            connectionFactory.getConnection().getPort()
        );
        
        // 返回 Redis 向量存储
        return new org.springframework.ai.vectorstore.redis.RedisVectorStore(
            embeddingModel,
            jedisPooled
        );
    }
}
