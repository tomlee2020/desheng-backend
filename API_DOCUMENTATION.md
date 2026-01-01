# Desheng Backend - API 文档

## 项目概述

本项目是为 `desheng-app` 前端应用开发的 Spring Boot 后端服务，提供种子审定信息的查询、搜索、筛选和分页功能。

## 技术栈

| 组件 | 版本 | 说明 |
| :--- | :--- | :--- |
| **Java** | 17 | 编程语言 |
| **Spring Boot** | 3.2.5 | Web 框架 |
| **Spring Data JPA** | - | 数据库 ORM |
| **MySQL** | 8.0+ | 关系型数据库（或 TiDB） |
| **Maven** | 3.8+ | 项目构建工具 |
| **Lombok** | - | 代码生成库（减少样板代码） |

## 项目结构

```
desheng-backend/
├── src/main/java/com/desheng/
│   ├── DeshengBackendApplication.java     # 主应用入口
│   ├── model/
│   │   ├── Seed.java                      # 种子实体类
│   │   └── SeedCharacteristics.java       # 种子特征类
│   ├── repository/
│   │   └── SeedRepository.java            # 数据访问层
│   ├── service/
│   │   └── SeedService.java               # 业务逻辑层
│   └── controller/
│       └── SeedController.java            # REST API 控制器
├── src/main/resources/
│   ├── application.properties             # 开发环境配置
│   ├── application-prod.properties        # 生产环境配置
│   ├── schema.sql                         # 数据库表定义
│   └── data.sql                           # 初始化数据
├── pom.xml                                # Maven 依赖配置
└── README.md                              # 项目说明
```

## 快速开始

### 1. 环境要求

- Java 17 或更高版本
- Maven 3.8 或更高版本
- MySQL 8.0 或 TiDB

### 2. 克隆项目

```bash
git clone <repository-url>
cd desheng-backend
```

### 3. 配置数据库

编辑 `src/main/resources/application.properties`（开发环境）或 `application-prod.properties`（生产环境）：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/desheng_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. 创建数据库

```sql
CREATE DATABASE desheng_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE desheng_db;
```

### 5. 构建项目

```bash
mvn clean install
```

### 6. 运行应用

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 端点

### 1. 获取所有种子（分页）

**请求**
```
GET /api/seeds?page=0&pageSize=10&sortBy=approvalYear&sortOrder=desc
```

**参数**
| 参数 | 类型 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- |
| `page` | int | 0 | 页码（0-indexed） |
| `pageSize` | int | 10 | 每页数量 |
| `sortBy` | string | approvalYear | 排序字段 |
| `sortOrder` | string | desc | 排序方向（asc/desc） |

**响应示例**
```json
{
  "content": [
    {
      "id": 1,
      "varietyName": "中国水稻 1 号",
      "approvalNumber": "ZZ2023001",
      "approvalYear": 2023,
      "approvalRegion": "全国",
      "cropType": "水稻",
      "company": "中国农业科学院",
      "companyPhone": "010-82109801",
      "companyAddress": "北京市朝阳区",
      "description": "...",
      "characteristics": "{...}",
      "adaptiveRegions": "[...]",
      "createdAt": "2023-01-15T00:00:00",
      "updatedAt": "2023-01-15T00:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 5,
  "totalPages": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 5,
  "first": true,
  "empty": false
}
```

### 2. 搜索种子

**请求**
```
GET /api/seeds/search?keyword=水稻&page=0&pageSize=10
```

**参数**
| 参数 | 类型 | 必需 | 说明 |
| :--- | :--- | :--- | :--- |
| `keyword` | string | 是 | 搜索关键词（品种名、审定号、企业名） |
| `page` | int | 否 | 页码（默认 0） |
| `pageSize` | int | 否 | 每页数量（默认 10） |
| `sortBy` | string | 否 | 排序字段 |
| `sortOrder` | string | 否 | 排序方向 |

**响应**：同获取所有种子的响应格式

### 3. 高级筛选

**请求**
```
GET /api/seeds/filter?cropType=水稻&approvalRegion=全国&startYear=2020&endYear=2023&company=隆平&page=0&pageSize=10
```

**参数**
| 参数 | 类型 | 必需 | 说明 |
| :--- | :--- | :--- | :--- |
| `cropType` | string | 否 | 作物类型 |
| `approvalRegion` | string | 否 | 审定地区 |
| `startYear` | int | 否 | 审定年份开始 |
| `endYear` | int | 否 | 审定年份结束 |
| `company` | string | 否 | 企业名称 |
| `page` | int | 否 | 页码 |
| `pageSize` | int | 否 | 每页数量 |
| `sortBy` | string | 否 | 排序字段 |
| `sortOrder` | string | 否 | 排序方向 |

**响应**：同获取所有种子的响应格式

### 4. 获取种子详情

**请求**
```
GET /api/seeds/{id}
```

**参数**
| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | long | 种子 ID |

**响应示例**
```json
{
  "id": 1,
  "varietyName": "中国水稻 1 号",
  "approvalNumber": "ZZ2023001",
  "approvalYear": 2023,
  "approvalRegion": "全国",
  "cropType": "水稻",
  "company": "中国农业科学院",
  "companyPhone": "010-82109801",
  "companyAddress": "北京市朝阳区",
  "description": "中国水稻 1 号是由中国农业科学院培育的优质高产水稻品种...",
  "characteristics": "{\"growthPeriod\":\"120-130 天\",\"yield\":\"600-700 kg/亩\",\"diseaseResistance\":\"抗稻瘟病、抗纹枯病\",\"qualityTraits\":\"优质、高产\"}",
  "adaptiveRegions": "[\"华东\",\"华中\",\"华南\"]",
  "createdAt": "2023-01-15T00:00:00",
  "updatedAt": "2023-01-15T00:00:00"
}
```

### 5. 创建新种子

**请求**
```
POST /api/seeds
Content-Type: application/json

{
  "varietyName": "新品种",
  "approvalNumber": "ZZ2024001",
  "approvalYear": 2024,
  "approvalRegion": "华北",
  "cropType": "小麦",
  "company": "某农业公司",
  "companyPhone": "010-12345678",
  "companyAddress": "北京市",
  "description": "新品种描述",
  "characteristics": "{\"growthPeriod\":\"200 天\",\"yield\":\"500 kg/亩\",\"diseaseResistance\":\"抗病\",\"qualityTraits\":\"优质\"}",
  "adaptiveRegions": "[\"华北\",\"华东\"]"
}
```

**响应**：201 Created，返回创建的种子对象

### 6. 更新种子

**请求**
```
PUT /api/seeds/{id}
Content-Type: application/json

{
  "varietyName": "更新后的品种名",
  "description": "更新后的描述"
}
```

**响应**：200 OK，返回更新后的种子对象

### 7. 删除种子

**请求**
```
DELETE /api/seeds/{id}
```

**响应**：204 No Content

## 与前端集成

### 修改前端 API 调用

在 `desheng-app` 中，修改 `lib/_core/api.ts` 或相关 API 调用文件：

```typescript
// 原来的 tRPC 调用改为 HTTP 调用
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const seedApi = {
  // 获取所有种子
  getAllSeeds: async (page = 0, pageSize = 10) => {
    const response = await axios.get(`${API_BASE_URL}/seeds`, {
      params: { page, pageSize }
    });
    return response.data;
  },

  // 搜索种子
  searchSeeds: async (keyword, page = 0, pageSize = 10) => {
    const response = await axios.get(`${API_BASE_URL}/seeds/search`, {
      params: { keyword, page, pageSize }
    });
    return response.data;
  },

  // 高级筛选
  filterSeeds: async (filters, page = 0, pageSize = 10) => {
    const response = await axios.get(`${API_BASE_URL}/seeds/filter`, {
      params: { ...filters, page, pageSize }
    });
    return response.data;
  },

  // 获取种子详情
  getSeedById: async (id) => {
    const response = await axios.get(`${API_BASE_URL}/seeds/${id}`);
    return response.data;
  }
};
```

### 更新 SeedContext

修改 `lib/seed-context.tsx` 以使用真实 API：

```typescript
const loadSeeds = useCallback(async () => {
  setIsLoading(true);
  try {
    const data = await seedApi.getAllSeeds(0, 100);
    setSeeds(data.content); // Spring Data Page 返回 content 字段
  } catch (error) {
    console.error('Failed to load seeds:', error);
  } finally {
    setIsLoading(false);
  }
}, []);

const searchSeeds = useCallback(async (query: string) => {
  setSearchQuery(query);
  if (query.trim()) {
    try {
      const data = await seedApi.searchSeeds(query);
      setSeeds(data.content);
    } catch (error) {
      console.error('Search failed:', error);
    }
  }
}, []);
```

## 数据库配置

### MySQL 配置示例

```sql
-- 创建数据库
CREATE DATABASE desheng_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE desheng_db;

-- 创建表（自动执行 schema.sql）
-- 插入初始数据（自动执行 data.sql）
```

### TiDB 配置示例

TiDB 兼容 MySQL 协议，只需修改连接字符串：

```properties
spring.datasource.url=jdbc:mysql://tidb-host:4000/desheng_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root@cluster
spring.datasource.password=password
```

## 部署指南

### 打包应用

```bash
mvn clean package
```

生成的 JAR 文件位于 `target/desheng-backend-0.0.1-SNAPSHOT.jar`

### 运行 JAR

```bash
java -jar target/desheng-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/desheng-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

构建镜像：

```bash
docker build -t desheng-backend:latest .
```

运行容器：

```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/desheng_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  desheng-backend:latest
```

## 性能优化建议

1. **数据库索引**：已在 `schema.sql` 中为常用查询字段添加索引
2. **分页查询**：所有列表接口均支持分页，避免一次性加载大量数据
3. **缓存策略**：可考虑使用 Redis 缓存热点数据
4. **连接池**：已配置 HikariCP 连接池，参数可根据负载调整

## 常见问题

### Q: 如何修改数据库连接？
A: 修改 `application.properties` 或 `application-prod.properties` 中的数据源配置。

### Q: 如何添加新的查询条件？
A: 在 `SeedRepository` 中添加新的查询方法，然后在 `SeedService` 中实现对应的业务逻辑。

### Q: 如何处理 CORS 问题？
A: `SeedController` 已添加 `@CrossOrigin` 注解，允许所有来源的请求。

## 许可证

MIT License
