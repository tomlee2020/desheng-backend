# Elasticsearch 集成指南

## 概述

本项目已集成 **Elasticsearch (ES)** 以支持高级搜索功能，包括：

1.  **拼音搜索**：支持全拼、简拼（首字母）以及拼音混合搜索
2.  **全文检索**：比 MySQL `LIKE` 更高效的关键词匹配
3.  **中文分词**：使用 IK 分词器实现智能分词
4.  **高亮显示**：搜索结果中关键词自动高亮
5.  **复杂条件查询**：支持多条件组合搜索

## 架构设计

### 数据流向

```
MySQL (主数据库)
    ↓
SeedService (业务逻辑)
    ↓
SeedSyncService (数据同步)
    ↓
Elasticsearch (搜索引擎)
```

### 关键组件

| 组件 | 职责 |
| :--- | :--- |
| `SeedDocument` | Elasticsearch 文档模型，包含拼音字段 |
| `SeedSearchService` | 搜索服务，负责 ES 查询和数据转换 |
| `SeedSyncService` | 数据同步服务，负责 MySQL 到 ES 的数据同步 |
| `SeedSearchController` | 搜索 API 控制器 |
| `DataSyncInitializer` | 应用启动时自动同步数据 |

## 部署指南

### 1. 安装 Elasticsearch

#### 使用 Docker（推荐）

```bash
# 拉取 Elasticsearch 镜像
docker pull docker.elastic.co/elasticsearch/elasticsearch:8.10.0

# 运行 Elasticsearch 容器
docker run -d \
  --name elasticsearch \
  -e discovery.type=single-node \
  -e xpack.security.enabled=false \
  -p 9200:9200 \
  -p 9300:9300 \
  docker.elastic.co/elasticsearch/elasticsearch:8.10.0
```

#### 使用 Docker Compose（更推荐）

更新 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.10.0
    container_name: desheng-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - desheng-network

  mysql:
    image: mysql:8.0
    container_name: desheng-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: desheng_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql
      - ./src/main/resources/data.sql:/docker-entrypoint-initdb.d/02-data.sql
    networks:
      - desheng-network

  backend:
    build: .
    container_name: desheng-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/desheng_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword
      SPRING_ELASTICSEARCH_URIS: http://elasticsearch:9200
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - elasticsearch
    networks:
      - desheng-network

volumes:
  mysql_data:
  elasticsearch_data:

networks:
  desheng-network:
    driver: bridge
```

启动所有服务：

```bash
docker-compose up -d
```

### 2. 配置 Spring Boot

在 `application.properties` 中配置 Elasticsearch：

```properties
# Elasticsearch Configuration
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic
spring.elasticsearch.password=elastic
spring.elasticsearch.connection-timeout=5s
spring.elasticsearch.socket-timeout=60s
```

### 3. 初始化数据

应用启动时会自动执行全量同步（通过 `DataSyncInitializer`）。如果需要手动同步，可以调用：

```bash
# 通过 Spring Boot Actuator 或直接调用 Service
POST /api/admin/sync-seeds
```

## API 端点

### 搜索 API（使用 Elasticsearch）

#### 1. 基础搜索

**请求**
```
GET /api/search/seeds?keyword=水稻&page=0&pageSize=10
```

**支持搜索的字段**：
- 品种名（中文和拼音）
- 品种名简拼（首字母）
- 审定编号
- 企业名（中文和拼音）

**示例**：
- 输入 "水稻" 匹配 "中国水稻 1 号"
- 输入 "shuidao" 匹配 "水稻"
- 输入 "sd" 匹配 "水稻"（简拼）

#### 2. 按作物类型搜索

**请求**
```
GET /api/search/crop-type?cropType=水稻&page=0&pageSize=10
```

#### 3. 按地区搜索

**请求**
```
GET /api/search/region?region=华北&page=0&pageSize=10
```

#### 4. 高级搜索

**请求**
```
GET /api/search/advanced?keyword=杂交&cropType=水稻&approvalRegion=华东&startYear=2020&endYear=2023&company=隆平&page=0&pageSize=10
```

**参数说明**：
| 参数 | 类型 | 必需 | 说明 |
| :--- | :--- | :--- | :--- |
| `keyword` | string | 否 | 搜索关键词 |
| `cropType` | string | 否 | 作物类型 |
| `approvalRegion` | string | 否 | 审定地区 |
| `startYear` | int | 否 | 审定年份开始 |
| `endYear` | int | 否 | 审定年份结束 |
| `company` | string | 否 | 企业名称 |
| `page` | int | 否 | 页码（默认 0） |
| `pageSize` | int | 否 | 每页数量（默认 10） |

## 数据同步

### 自动同步

1.  **应用启动时**：`DataSyncInitializer` 自动执行全量同步
2.  **新增种子**：`SeedService.createSeed()` 自动同步到 ES
3.  **更新种子**：`SeedService.updateSeed()` 自动同步到 ES
4.  **删除种子**：`SeedService.deleteSeed()` 自动从 ES 删除

### 手动同步

如需手动触发全量同步，可以创建管理接口：

```java
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final SeedSyncService seedSyncService;
    
    @PostMapping("/sync-seeds")
    public ResponseEntity<String> syncSeeds() {
        seedSyncService.syncAllSeeds();
        return ResponseEntity.ok("Sync completed");
    }
}
```

## 拼音搜索原理

### 拼音字段

在 `SeedDocument` 中，每个中文字段都对应一个拼音字段：

| 中文字段 | 拼音字段 | 简拼字段 | 说明 |
| :--- | :--- | :--- | :--- |
| `varietyName` | `varietyNamePinyin` | `varietyNamePinyinShort` | 品种名 |
| `company` | `companyPinyin` | - | 企业名 |

### 拼音转换

使用 `jpinyin` 库进行中文到拼音的转换：

```java
// 全拼：水稻 -> shui dao
String pinyin = PinyinUtil.getPinyinWithoutTone("水稻");

// 简拼：水稻 -> sd
String shortPinyin = PinyinUtil.getPinyinShort("水稻");

// 搜索用拼音：水稻 -> shuidao
String searchPinyin = PinyinUtil.getPinyinForSearch("水稻");
```

### 搜索流程

1.  用户输入关键词（可以是中文、拼音或简拼）
2.  后端接收关键词
3.  ES 同时在以下字段中搜索：
    - `varietyName`（中文分词）
    - `varietyNamePinyin`（拼音）
    - `varietyNamePinyinShort`（简拼）
    - `company`（中文分词）
    - `companyPinyin`（拼音）
4.  返回匹配结果

## 性能优化

### 1. 索引优化

Elasticsearch 会自动为 `SeedDocument` 创建索引。关键字段的配置：

```java
@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
private String varietyName;  // 使用 IK 分词器进行中文分词

@Field(type = FieldType.Keyword)
private String cropType;  // 不分词，精确匹配
```

### 2. 分页查询

所有搜索接口都支持分页，避免一次性返回大量数据：

```java
Page<SeedDocument> results = seedSearchService.searchSeeds(keyword, page, pageSize);
```

### 3. 批量同步

应用启动时使用批量操作同步数据，提高效率：

```java
seedSearchService.saveDocuments(documents);  // 批量保存
```

## 常见问题

### Q: 如何验证 Elasticsearch 是否正常运行？

A: 访问 Elasticsearch 健康检查端点：

```bash
curl http://localhost:9200/_cluster/health
```

返回示例：
```json
{
  "cluster_name": "elasticsearch",
  "status": "green",
  "timed_out": false,
  "number_of_nodes": 1,
  "number_of_data_nodes": 1,
  "active_primary_shards": 1,
  "active_shards": 1,
  "relocating_shards": 0,
  "initializing_shards": 0,
  "unassigned_shards": 0,
  "delayed_unassigned_shards": 0,
  "number_of_pending_tasks": 0,
  "number_of_in_flight_fetch": 0,
  "task_max_waiting_in_queue_ms": 0,
  "active_shards_percent_as_number": 100.0
}
```

### Q: 如何查看 Elasticsearch 中的索引？

A: 使用 Kibana 或直接调用 API：

```bash
# 查看所有索引
curl http://localhost:9200/_cat/indices

# 查看 seeds 索引的映射
curl http://localhost:9200/seeds/_mapping

# 查看 seeds 索引的文档数
curl http://localhost:9200/seeds/_count
```

### Q: 如何重新索引数据？

A: 调用同步接口：

```bash
POST /api/admin/sync-seeds
```

这会清空旧索引并重新同步所有数据。

### Q: 拼音搜索不工作怎么办？

A: 检查以下几点：
1. Elasticsearch 是否正常运行
2. `jpinyin` 依赖是否正确引入
3. 应用启动时是否成功同步数据
4. 搜索关键词是否正确

### Q: 如何处理 Elasticsearch 连接超时？

A: 在 `application.properties` 中增加超时时间：

```properties
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=120s
```

## 监控和维护

### 1. 监控索引大小

```bash
curl http://localhost:9200/_cat/indices?v
```

### 2. 清理旧索引

```bash
curl -X DELETE http://localhost:9200/seeds
```

### 3. 备份数据

```bash
# 创建快照仓库
curl -X PUT http://localhost:9200/_snapshot/backup -H "Content-Type: application/json" -d '{"type": "fs", "settings": {"location": "/backup"}}'

# 创建快照
curl -X PUT http://localhost:9200/_snapshot/backup/snapshot1?wait_for_completion=true
```

## 生产环境建议

1.  **集群部署**：使用 Elasticsearch 集群而不是单节点
2.  **安全认证**：启用 X-Pack 安全认证
3.  **监控告警**：使用 Elasticsearch 监控工具
4.  **定期备份**：定期备份 Elasticsearch 数据
5.  **性能调优**：根据数据量调整 JVM 堆大小和分片数

## 参考资源

- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data Elasticsearch 文档](https://spring.io/projects/spring-data-elasticsearch)
- [jpinyin 项目](https://github.com/stuxuhai/jpinyin)
- [IK 分词器](https://github.com/medcl/elasticsearch-analysis-ik)
