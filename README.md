# Desheng Backend - Spring Boot 后端服务

## 项目简介

本项目是为 **desheng-app**（种子审定查询系统）开发的 Spring Boot 后端服务。它提供了一套完整的 RESTful API，支持种子数据的查询、搜索、高级筛选和分页功能，旨在替代原有的 Node.js 模拟后端。

## 核心特性

✅ **RESTful API**：遵循 REST 设计原则，接口清晰易用  
✅ **分页查询**：支持大数据量的高效查询  
✅ **多条件筛选**：支持按作物类型、地区、年份、企业等多维度筛选  
✅ **全文搜索**：支持品种名、审定号、企业名的模糊搜索  
✅ **数据库优化**：使用 MySQL 索引和 JPA 优化查询性能  
✅ **生产就绪**：包含生产环境配置、日志管理和错误处理  

## 快速开始

### 前置要求

- **Java 17** 或更高版本
- **Maven 3.8** 或更高版本
- **MySQL 8.0** 或 **TiDB**

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd desheng-backend
   ```

2. **配置数据库**
   
   编辑 `src/main/resources/application.properties`：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/desheng_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **构建项目**
   ```bash
   mvn clean install
   ```

4. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

   应用将在 `http://localhost:8080` 启动。

## API 端点概览

| 方法 | 端点 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/seeds` | 获取所有种子（分页） |
| GET | `/api/seeds/search?keyword=...` | 搜索种子 |
| GET | `/api/seeds/filter?...` | 高级筛选 |
| GET | `/api/seeds/{id}` | 获取种子详情 |
| POST | `/api/seeds` | 创建新种子 |
| PUT | `/api/seeds/{id}` | 更新种子 |
| DELETE | `/api/seeds/{id}` | 删除种子 |

详细 API 文档请参考 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 项目结构

```
desheng-backend/
├── src/main/java/com/desheng/
│   ├── DeshengBackendApplication.java     # 应用入口
│   ├── model/                             # 数据模型
│   │   ├── Seed.java
│   │   └── SeedCharacteristics.java
│   ├── repository/                        # 数据访问层
│   │   └── SeedRepository.java
│   ├── service/                           # 业务逻辑层
│   │   └── SeedService.java
│   └── controller/                        # REST 控制器
│       └── SeedController.java
├── src/main/resources/
│   ├── application.properties             # 开发环境配置
│   ├── application-prod.properties        # 生产环境配置
│   ├── schema.sql                         # 数据库表定义
│   └── data.sql                           # 初始化数据
├── pom.xml                                # Maven 依赖配置
├── API_DOCUMENTATION.md                   # 详细 API 文档
└── README.md                              # 本文件
```

## 技术栈

| 技术 | 版本 | 用途 |
| :--- | :--- | :--- |
| Spring Boot | 3.2.5 | Web 框架 |
| Spring Data JPA | - | ORM 框架 |
| MySQL Connector | - | 数据库驱动 |
| Lombok | - | 代码生成 |
| Maven | 3.8+ | 构建工具 |

## 与前端集成

### 修改前端配置

在 `desheng-app` 的 `lib/_core/api.ts` 中修改 API 调用：

```typescript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const seedApi = {
  getAllSeeds: (page = 0, pageSize = 10) =>
    axios.get(`${API_BASE_URL}/seeds`, { params: { page, pageSize } }),
  
  searchSeeds: (keyword, page = 0, pageSize = 10) =>
    axios.get(`${API_BASE_URL}/seeds/search`, { params: { keyword, page, pageSize } }),
  
  filterSeeds: (filters, page = 0, pageSize = 10) =>
    axios.get(`${API_BASE_URL}/seeds/filter`, { params: { ...filters, page, pageSize } }),
  
  getSeedById: (id) =>
    axios.get(`${API_BASE_URL}/seeds/${id}`)
};
```

### 更新 SeedContext

修改 `lib/seed-context.tsx` 以使用真实 API 而非模拟数据。

## 部署

### 本地开发

```bash
mvn spring-boot:run
```

### 生产环境

1. **打包应用**
   ```bash
   mvn clean package -DskipTests
   ```

2. **运行 JAR**
   ```bash
   java -jar target/desheng-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

3. **Docker 部署**
   ```bash
   docker build -t desheng-backend:latest .
   docker run -d -p 8080:8080 desheng-backend:latest
   ```

## 性能优化

- ✅ 数据库索引优化（已在 `schema.sql` 中配置）
- ✅ 分页查询避免一次性加载大量数据
- ✅ HikariCP 连接池配置
- ✅ 支持 Redis 缓存（可选）

## 常见问题

**Q: 如何修改数据库连接信息？**  
A: 编辑 `application.properties` 或 `application-prod.properties`

**Q: 如何添加新的查询条件？**  
A: 在 `SeedRepository` 中添加查询方法，在 `SeedService` 中实现业务逻辑

**Q: 如何处理 CORS 问题？**  
A: `SeedController` 已配置 `@CrossOrigin` 允许跨域请求

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。
