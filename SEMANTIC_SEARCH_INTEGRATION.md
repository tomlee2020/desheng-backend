# Spring Boot 后端语义搜索集成指南

## 概述

本文档说明了如何在现有的 `desheng-backend` 项目中使用新增的语义搜索功能。语义搜索基于 **Spring AI** 和 **Redis 向量存储**，能够理解用户的真实意图，提供比传统关键词搜索更准确的结果。

## 架构

### 多层搜索架构

```
用户查询
  ↓
┌─────────────────────────────────────┐
│  关键词搜索 (Elasticsearch)          │  → 精确匹配、拼音搜索
├─────────────────────────────────────┤
│  语义搜索 (Spring AI + Redis)        │  → 意图理解、相似度检索
├─────────────────────────────────────┤
│  结构化查询 (MyBatis-Plus)           │  → 精确条件筛选
└─────────────────────────────────────┘
  ↓
结果聚合与排序
  ↓
返回给用户
```

## 新增组件

### 1. 依赖更新

在 `pom.xml` 中新增了以下依赖：

```xml
<!-- Spring AI Core -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-core</artifactId>
    <version>0.8.1</version>
</dependency>

<!-- Spring AI OpenAI Embedding -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai</artifactId>
    <version>0.8.1</version>
</dependency>

<!-- Spring AI Redis Vector Store -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-redis</artifactId>
    <version>0.8.1</version>
</dependency>

<!-- Redis Client -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 2. 配置文件更新

在 `application.properties` 中新增了：

```properties
# Spring AI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.base-url=https://api.openai.com/v1
spring.ai.embedding.openai.model=text-embedding-3-small

# Redis Configuration for Vector Store
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.timeout=60000ms
```

### 3. 新增类

| 类名 | 位置 | 职责 |
| :--- | :--- | :--- |
| `SpringAiConfig` | `config/` | 配置 Spring AI 和 Redis 向量存储 |
| `VectorIndexInitializer` | `config/` | 应用启动时自动初始化向量索引 |
| `SeedVector` | `model/` | 向量化种子模型 |
| `SemanticSearchService` | `service/` | 语义搜索业务逻辑 |
| `SemanticSearchController` | `controller/` | 语义搜索 API 端点 |

## 快速开始

### 1. 环境准备

#### 安装 Redis

**使用 Docker**：

```bash
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine
```

**或本地安装**：

```bash
# macOS
brew install redis

# Ubuntu
sudo apt-get install redis-server

# 启动 Redis
redis-server
```

#### 设置 OpenAI API Key

```bash
export OPENAI_API_KEY=your_openai_api_key_here
```

### 2. 启动应用

```bash
mvn clean package
java -jar target/desheng-backend-0.0.1-SNAPSHOT.jar
```

应用启动时会自动初始化向量索引。如果初始化失败，可以手动调用初始化接口。

### 3. 手动初始化索引（可选）

如果应用启动时未能自动初始化，可以手动调用：

```bash
curl -X POST http://localhost:8080/api/semantic-search/index
```

## API 端点

### 1. 初始化索引

**请求**
```
POST /api/semantic-search/index
```

**响应**
```json
"Seeds indexed successfully for semantic search"
```

**说明**：
- 将所有种子数据向量化并存储到 Redis
- 这是一个初始化操作，通常只需执行一次
- 应用启动时会自动执行

### 2. 语义搜索

**请求**
```
GET /api/semantic-search/search?query=抗倒伏的水稻品种&topK=5
```

**参数说明**

| 参数 | 类型 | 必需 | 说明 |
| :--- | :--- | :--- | :--- |
| `query` | string | 是 | 查询文本（支持自然语言） |
| `topK` | int | 否 | 返回结果数（默认 10，最大 100） |

**响应示例**
```json
[
  {
    "seedId": 1,
    "varietyName": "中国水稻 1 号",
    "approvalNumber": "LS-2020-001",
    "cropType": "水稻",
    "company": "隆平高科",
    "content": "品种名: 中国水稻 1 号 作物类型: 水稻 企业: 隆平高科 ...",
    "similarity": 0.92
  },
  {
    "seedId": 2,
    "varietyName": "杂交水稻 2 号",
    "approvalNumber": "LS-2021-002",
    "cropType": "水稻",
    "company": "中国水稻所",
    "content": "品种名: 杂交水稻 2 号 作物类型: 水稻 企业: 中国水稻所 ...",
    "similarity": 0.88
  }
]
```

**说明**：
- 返回按相似度从高到低排序的种子列表
- `similarity` 字段表示与查询的相似度（0-1，值越大越相似）

### 3. 搜索示例

**请求**
```
GET /api/semantic-search/example
```

**响应**
```
语义搜索示例：

1. 搜索抗倒伏的水稻品种：
   /api/semantic-search/search?query=抗倒伏的水稻品种&topK=5

2. 搜索高产量玉米：
   /api/semantic-search/search?query=高产量玉米&topK=10

3. 搜索适合华东地区的大豆：
   /api/semantic-search/search?query=华东地区大豆&topK=5

...
```

## 使用场景

### 场景 1：用户输入自然语言查询

**用户查询**："我需要一个在华东地区种植，产量高，抗病性强的水稻品种"

**传统搜索**：
- 需要用户手动输入多个筛选条件
- 或者需要用户精确输入关键词

**语义搜索**：
- 直接理解用户的完整需求
- 返回最符合条件的品种

```bash
curl "http://localhost:8080/api/semantic-search/search?query=华东地区产量高抗病性强的水稻品种&topK=5"
```

### 场景 2：用户输入了错别字

**用户查询**："抗倒伏的水稻品中"（最后一个字错了）

**传统搜索**：
- 无法找到任何结果

**语义搜索**：
- 能理解用户想要"抗倒伏的水稻品种"
- 返回相关的水稻品种

```bash
curl "http://localhost:8080/api/semantic-search/search?query=抗倒伏的水稻品中&topK=5"
```

### 场景 3：用户使用近义词

**用户查询**："抗倒性强的水稻"

**传统搜索**：
- 如果数据库中存储的是"抗倒伏"，可能搜不到

**语义搜索**：
- 能理解"抗倒性强"和"抗倒伏"是同义的
- 返回相关的水稻品种

```bash
curl "http://localhost:8080/api/semantic-search/search?query=抗倒性强的水稻&topK=5"
```

## 与现有搜索功能的对比

### 关键词搜索 (Elasticsearch)

**优势**：
- 精确匹配
- 支持拼音搜索
- 支持分词搜索
- 性能极快

**劣势**：
- 无法理解用户意图
- 对拼写错误敏感
- 无法识别同义词

**适用场景**：
- 用户知道确切的品种名或企业名
- 用户想要精确搜索

### 语义搜索 (Spring AI)

**优势**：
- 理解用户意图
- 容错能力强
- 能识别同义词
- 支持自然语言查询

**劣势**：
- 相对较慢（需要向量计算）
- 需要 OpenAI API 调用
- 存储空间较大

**适用场景**：
- 用户想要模糊搜索
- 用户输入了错别字
- 用户使用了非标准表达

### 混合搜索建议

为了获得最佳的用户体验，建议在前端同时调用两个搜索接口，然后合并结果：

```javascript
// 前端代码示例
async function searchSeeds(query) {
  // 1. 关键词搜索
  const keywordResults = await fetch(
    `/api/search/seeds?keyword=${query}`
  ).then(r => r.json());
  
  // 2. 语义搜索
  const semanticResults = await fetch(
    `/api/semantic-search/search?query=${query}`
  ).then(r => r.json());
  
  // 3. 合并结果（去重并按相关性排序）
  const combined = mergResults(keywordResults, semanticResults);
  
  return combined;
}
```

## 性能优化

### 1. 向量维度

- `text-embedding-3-small`：1536 维，精度中等，速度快
- `text-embedding-3-large`：3072 维，精度高，速度慢

**建议**：默认使用 `text-embedding-3-small`，如需更高精度可升级。

### 2. Redis 优化

```properties
# 增加 Redis 连接池大小
spring.data.redis.jedis.pool.max-active=16
spring.data.redis.jedis.pool.max-idle=8

# 增加超时时间
spring.data.redis.timeout=120000ms
```

### 3. 缓存热门查询

可以在 `SemanticSearchService` 中添加缓存：

```java
@Cacheable(value = "semanticSearch", key = "#query")
public List<SeedVector> semanticSearch(String query, int topK) {
    // ...
}
```

## 故障排除

### 问题 1：Redis 连接失败

**错误信息**：`Cannot get a resource from the pool`

**解决方案**：
1. 检查 Redis 是否正在运行
2. 检查 `application.properties` 中的 Redis 配置
3. 检查防火墙是否允许连接

### 问题 2：OpenAI API 超时

**错误信息**：`Timeout waiting for connection from pool`

**解决方案**：
1. 检查网络连接
2. 增加超时时间：`spring.ai.openai.timeout=60s`
3. 检查 API Key 是否有效

### 问题 3：向量索引初始化失败

**错误信息**：`Failed to index seeds`

**解决方案**：
1. 检查 MySQL 是否有数据
2. 检查 Redis 是否可用
3. 手动调用 `POST /api/semantic-search/index`

### 问题 4：搜索结果不准确

**原因**：
1. 向量索引未初始化
2. 查询文本过于简短或模糊
3. 数据库中的种子描述不够详细

**解决方案**：
1. 确保已调用 `/api/semantic-search/index`
2. 使用更详细的查询文本
3. 增加种子数据中的描述信息

## 下一步

### 1. 集成前端

在 React Native 前端中调用新的语义搜索接口：

```typescript
// 前端代码示例
const semanticSearch = async (query: string) => {
  const response = await fetch(
    `http://backend-url/api/semantic-search/search?query=${query}&topK=10`
  );
  return response.json();
};
```

### 2. 添加图片搜索

基于当前的语义搜索架构，可以进一步扩展为多模态搜索（图片 + 文本）。

### 3. 性能监控

添加监控和日志，跟踪搜索性能和用户行为。

## 参考资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI Embedding API](https://platform.openai.com/docs/guides/embeddings)
- [Redis 官方文档](https://redis.io/documentation)
- [向量搜索原理](https://www.pinecone.io/learn/vector-database/)
