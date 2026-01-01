# Desheng Backend 与 Frontend 集成指南

## 概述

本文档提供了如何将 Spring Boot 后端与 desheng-app 前端应用集成的详细步骤。

## 集成步骤

### 第一步：启动后端服务

#### 方式一：本地运行

```bash
# 1. 进入后端项目目录
cd desheng-backend

# 2. 构建项目
mvn clean install

# 3. 运行应用
mvn spring-boot:run
```

后端将在 `http://localhost:8080` 启动。

#### 方式二：Docker 运行

```bash
# 1. 使用 docker-compose 启动 MySQL 和后端
docker-compose up -d

# 2. 检查服务状态
docker-compose ps

# 3. 查看后端日志
docker-compose logs -f backend
```

### 第二步：修改前端 API 配置

#### 2.1 创建或修改 API 客户端

在 `desheng-app/lib/_core/api.ts` 中创建 HTTP 客户端：

```typescript
import axios from 'axios';

// 根据环境配置 API 基础 URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 响应拦截器处理错误
apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

export default apiClient;
```

#### 2.2 创建种子 API 服务

在 `desheng-app/lib/_core/seedApi.ts` 中创建：

```typescript
import apiClient from './api';

export interface SeedListResponse {
  content: Seed[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export const seedApi = {
  /**
   * 获取所有种子（分页）
   */
  getAllSeeds: async (page: number = 0, pageSize: number = 10, sortBy?: string, sortOrder?: string) => {
    const response = await apiClient.get('/seeds', {
      params: { page, pageSize, sortBy, sortOrder },
    });
    return response.data;
  },

  /**
   * 搜索种子
   */
  searchSeeds: async (keyword: string, page: number = 0, pageSize: number = 10) => {
    const response = await apiClient.get('/seeds/search', {
      params: { keyword, page, pageSize },
    });
    return response.data;
  },

  /**
   * 高级筛选
   */
  filterSeeds: async (
    filters: {
      cropType?: string;
      approvalRegion?: string;
      startYear?: number;
      endYear?: number;
      company?: string;
    },
    page: number = 0,
    pageSize: number = 10,
    sortBy?: string,
    sortOrder?: string
  ) => {
    const response = await apiClient.get('/seeds/filter', {
      params: { ...filters, page, pageSize, sortBy, sortOrder },
    });
    return response.data;
  },

  /**
   * 获取种子详情
   */
  getSeedById: async (id: number) => {
    const response = await apiClient.get(`/seeds/${id}`);
    return response.data;
  },

  /**
   * 创建种子
   */
  createSeed: async (seed: Seed) => {
    const response = await apiClient.post('/seeds', seed);
    return response.data;
  },

  /**
   * 更新种子
   */
  updateSeed: async (id: number, seed: Partial<Seed>) => {
    const response = await apiClient.put(`/seeds/${id}`, seed);
    return response.data;
  },

  /**
   * 删除种子
   */
  deleteSeed: async (id: number) => {
    await apiClient.delete(`/seeds/${id}`);
  },
};
```

### 第三步：更新 SeedContext

修改 `desheng-app/lib/seed-context.tsx` 以使用真实 API：

```typescript
import React, { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { seedApi } from './_core/seedApi';

// ... 保留原有的类型定义 ...

export function SeedProvider({ children }: { children: React.ReactNode }) {
  const [seeds, setSeeds] = useState<Seed[]>([]);
  const [favorites, setFavorites] = useState<Seed[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFiltersState] = useState<FilterOptions>({});
  const [sortOptions, setSortOptionsState] = useState<SortOptions>({
    field: 'approvalYear',
    order: 'desc',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);

  // 初始化
  useEffect(() => {
    loadFavorites();
    loadSeeds();
  }, []);

  // 加载收藏列表
  const loadFavorites = async () => {
    try {
      const saved = await AsyncStorage.getItem('seedFavorites');
      if (saved) {
        setFavorites(JSON.parse(saved));
      }
    } catch (error) {
      console.error('Failed to load favorites:', error);
    }
  };

  // 加载种子数据
  const loadSeeds = useCallback(async (page: number = 0) => {
    setIsLoading(true);
    try {
      const data = await seedApi.getAllSeeds(page, 10, sortOptions.field, sortOptions.order);
      setSeeds(data.content || []);
      setTotalPages(data.totalPages || 0);
      setCurrentPage(data.number || 0);
    } catch (error) {
      console.error('Failed to load seeds:', error);
    } finally {
      setIsLoading(false);
    }
  }, [sortOptions]);

  // 搜索种子
  const searchSeeds = useCallback(async (query: string) => {
    setSearchQuery(query);
    if (!query.trim()) {
      loadSeeds();
      return;
    }

    setIsLoading(true);
    try {
      const data = await seedApi.searchSeeds(query, 0, 10);
      setSeeds(data.content || []);
      setTotalPages(data.totalPages || 0);
      setCurrentPage(0);
    } catch (error) {
      console.error('Search failed:', error);
    } finally {
      setIsLoading(false);
    }
  }, [loadSeeds]);

  // 应用筛选条件
  const applyFilters = useCallback(async (newFilters: FilterOptions, page: number = 0) => {
    setFiltersState(newFilters);
    setIsLoading(true);
    try {
      const data = await seedApi.filterSeeds(newFilters, page, 10, sortOptions.field, sortOptions.order);
      setSeeds(data.content || []);
      setTotalPages(data.totalPages || 0);
      setCurrentPage(page);
    } catch (error) {
      console.error('Filter failed:', error);
    } finally {
      setIsLoading(false);
    }
  }, [sortOptions]);

  // 清除筛选条件
  const clearFilters = useCallback(() => {
    setFiltersState({});
    loadSeeds();
  }, [loadSeeds]);

  // 设置排序选项
  const setSortOptions = useCallback((newSortOptions: SortOptions) => {
    setSortOptionsState(newSortOptions);
    loadSeeds(0);
  }, [loadSeeds]);

  // 过滤和排序（本地处理，可选）
  const filteredSeeds = useMemo(() => {
    return seeds;
  }, [seeds]);

  // 添加收藏
  const addFavorite = useCallback(
    async (seed: Seed) => {
      if (!favorites.find(f => f.id === seed.id)) {
        const newFavorites = [...favorites, seed];
        setFavorites(newFavorites);
        try {
          await AsyncStorage.setItem('seedFavorites', JSON.stringify(newFavorites));
        } catch (error) {
          console.error('Failed to save favorites:', error);
        }
      }
    },
    [favorites]
  );

  // 移除收藏
  const removeFavorite = useCallback(
    async (seedId: string | number) => {
      const newFavorites = favorites.filter(f => f.id !== seedId);
      setFavorites(newFavorites);
      try {
        await AsyncStorage.setItem('seedFavorites', JSON.stringify(newFavorites));
      } catch (error) {
        console.error('Failed to update favorites:', error);
      }
    },
    [favorites]
  );

  // 检查是否已收藏
  const isFavorited = useCallback(
    (seedId: string | number) => {
      return favorites.some(f => f.id === seedId);
    },
    [favorites]
  );

  // 根据 ID 获取种子
  const getSeedById = useCallback(
    async (id: number) => {
      try {
        return await seedApi.getSeedById(id);
      } catch (error) {
        console.error('Failed to get seed:', error);
        return undefined;
      }
    },
    []
  );

  // 加载下一页
  const loadNextPage = useCallback(() => {
    if (currentPage < totalPages - 1) {
      loadSeeds(currentPage + 1);
    }
  }, [currentPage, totalPages, loadSeeds]);

  return (
    <SeedContext.Provider
      value={{
        seeds,
        filteredSeeds,
        favorites,
        searchQuery,
        filters,
        sortOptions,
        isLoading,
        totalPages,
        currentPage,
        searchSeeds,
        setFilters: applyFilters,
        clearFilters,
        setSortOptions,
        addFavorite,
        removeFavorite,
        isFavorited,
        loadSeeds,
        getSeedById,
        loadNextPage,
      }}
    >
      {children}
    </SeedContext.Provider>
  );
}

export function useSeeds() {
  const context = useContext(SeedContext);
  if (!context) {
    throw new Error('useSeeds must be used within SeedProvider');
  }
  return context;
}
```

### 第四步：更新环境变量

在 `desheng-app/.env` 中添加：

```env
REACT_APP_API_URL=http://localhost:8080/api
```

对于生产环境，在 `.env.production` 中：

```env
REACT_APP_API_URL=https://api.yourdomain.com/api
```

### 第五步：测试集成

1. **启动后端服务**
   ```bash
   cd desheng-backend
   mvn spring-boot:run
   ```

2. **启动前端应用**
   ```bash
   cd desheng-app
   pnpm dev
   ```

3. **测试 API 调用**
   - 打开浏览器开发者工具
   - 检查 Network 标签中的 API 请求
   - 验证响应数据是否正确

## 常见问题

### Q: 前端无法连接到后端
**A:** 检查以下几点：
- 后端服务是否正在运行（`http://localhost:8080`）
- 前端的 API 基础 URL 是否正确
- 浏览器控制台是否有 CORS 错误

### Q: CORS 错误
**A:** 后端已配置 `@CrossOrigin` 允许所有来源。如果仍有问题，检查：
- 后端是否正确启动
- 前端请求的 URL 是否正确

### Q: 数据为空
**A:** 检查：
- 数据库是否已初始化（运行 `schema.sql` 和 `data.sql`）
- 后端日志中是否有错误信息

### Q: 性能问题
**A:** 优化建议：
- 使用分页查询而不是一次性加载所有数据
- 在前端实现虚拟滚动（Virtual Scrolling）
- 考虑使用 Redis 缓存热点数据

## 生产环境部署

### 后端部署

```bash
# 1. 打包应用
mvn clean package -DskipTests

# 2. 使用 Docker 部署
docker build -t desheng-backend:latest .
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/desheng_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  desheng-backend:latest
```

### 前端部署

```bash
# 1. 构建前端
pnpm build

# 2. 部署到 CDN 或 Web 服务器
# 根据您的部署平台选择相应的部署方式
```

## 性能监控

### 后端监控

启用 Spring Boot Actuator 进行性能监控：

在 `pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

访问 `http://localhost:8080/actuator` 查看监控信息。

### 前端监控

使用浏览器开发者工具或专业的 APM 工具（如 Sentry）监控前端性能。

## 支持

如有问题，请查看：
- [API 文档](./API_DOCUMENTATION.md)
- [README](./README.md)
- 后端日志：`logs/desheng-backend.log`
