# 推荐功能集成指南

## 概述

本文档介绍了"猜你喜欢"推荐功能的设计、实现和使用方法。该功能通过分析用户的搜索历史和行为，为用户推荐相关的种子品种。

## 推荐算法

推荐系统采用**混合推荐策略**，包含以下三个主要算法：

### 1. 基于内容的推荐 (Content-Based)
- **原理**：分析用户最近搜索的关键词，使用语义搜索找到相似的种子品种。
- **实现**：利用 Spring AI 的向量化能力，将用户搜索词转化为向量，在向量数据库中检索相似度最高的品种。
- **优势**：能够捕捉用户的实时搜索意图，推荐的品种与用户的直接搜索关键词高度相关。

### 2. 基于用户画像的推荐 (User Profile-Based)
- **原理**：分析用户搜索历史中出现频率最高的作物类型和地区，优先推荐符合用户偏好的品种。
- **实现**：统计用户搜索词中的作物类型和地区信息，构建用户画像，然后在数据库中查询匹配的品种。
- **优势**：能够反映用户的长期偏好，推荐的品种更符合用户的整体需求。

### 3. 热门品种推荐 (Trending)
- **原理**：推荐最新审定和搜索热度最高的品种。
- **实现**：按审定年份倒序查询，优先返回最近 1-2 年审定的品种。
- **优势**：对新用户（冷启动）友好，能够展示平台最优质的品种。

## API 接口

### 1. 获取个性化推荐

**请求**：
```
GET /api/recommend/guess-like?userId=user123&limit=6
```

**参数**：
- `userId` (可选)：用户唯一标识。如果为空，系统会生成一个临时 ID。
- `limit` (可选)：推荐数量，默认 6，最多 20。

**响应示例**：
```json
{
  "code": 200,
  "message": "Success",
  "count": 6,
  "data": [
    {
      "id": 101,
      "varietyName": "德胜 1 号",
      "cropType": "水稻",
      "approvalNumber": "ZS2023001",
      "approvalYear": 2023,
      "approvalRegion": "华北",
      "company": "德胜种业",
      "description": "高产、抗倒伏水稻品种",
      "characteristics": "{\"yield\": \"高产\", \"disease_resistance\": \"抗倒伏\"}",
      "reason": "基于您搜索过的\"高产水稻\"推荐",
      "score": 0.95,
      "recommendationType": "content-based"
    },
    {
      "id": 102,
      "varietyName": "丰收 2 号",
      "cropType": "水稻",
      "approvalNumber": "ZS2023002",
      "approvalYear": 2023,
      "approvalRegion": "华东",
      "company": "丰收农业",
      "description": "早熟、高产水稻品种",
      "characteristics": "{\"maturity\": \"早熟\", \"yield\": \"高产\"}",
      "reason": "基于您对水稻的关注推荐",
      "score": 0.88,
      "recommendationType": "user-profile"
    }
  ]
}
```

### 2. 冷启动推荐（新用户）

**请求**：
```
GET /api/recommend/cold-start?limit=6
```

**参数**：
- `limit` (可选)：推荐数量，默认 6，最多 20。

**响应**：与个性化推荐相同的格式。

### 3. 相似品种推荐

**请求**：
```
GET /api/recommend/similar/101?limit=5
```

**参数**：
- `seedId` (必填)：种子 ID，在 URL 路径中。
- `limit` (可选)：推荐数量，默认 5，最多 20。

**响应**：推荐与指定品种相似的其他品种。

### 4. 趋势推荐（热门品种）

**请求**：
```
GET /api/recommend/trending?limit=10
```

**参数**：
- `limit` (可选)：推荐数量，默认 10，最多 20。

**响应**：返回最新审定和最受欢迎的品种。

### 5. 推荐反馈

**请求**：
```
POST /api/recommend/feedback?userId=user123&seedId=101&feedback=like
```

**参数**：
- `userId` (必填)：用户 ID。
- `seedId` (必填)：种子 ID。
- `feedback` (必填)：反馈类型，可选值：`like`（喜欢）、`dislike`（不喜欢）、`click`（点击）。

**响应**：
```json
{
  "code": 200,
  "message": "Feedback received successfully",
  "data": null
}
```

## 前端集成

### 1. 在首页添加"猜你喜欢"模块

```typescript
import { recommendApi } from '../lib/api-client';

// 获取推荐
const recommendations = await recommendApi.getGuessLikeRecommendations(userId, 6);

// 渲染推荐列表
recommendations.data.forEach(rec => {
  console.log(`${rec.varietyName} - ${rec.reason}`);
});
```

### 2. 在种子详情页添加"相似品种"模块

```typescript
// 获取相似品种
const similarSeeds = await recommendApi.getSimilarSeeds(seedId, 5);
```

### 3. 提交推荐反馈

```typescript
// 用户点击推荐品种时
await recommendApi.submitFeedback(userId, seedId, 'click');

// 用户收藏推荐品种时
await recommendApi.submitFeedback(userId, seedId, 'like');
```

## 性能优化

### 1. 缓存策略
- 热搜榜单和趋势推荐可以缓存 1 小时。
- 用户的个性化推荐可以缓存 30 分钟。

### 2. 异步处理
- 推荐反馈可以异步提交，不阻塞用户交互。
- 用户画像的构建可以在后台定期更新。

### 3. 数据库优化
- 在 `search_history` 表上建立索引：`(user_id, created_at)`。
- 在 `seeds` 表上建立索引：`(crop_type, approval_region, approval_year)`。

## 算法改进方向

### 短期（1-2 个月）
1. 引入**协同过滤**：分析用户之间的相似性，推荐相似用户喜欢的品种。
2. 优化**关键词提取**：使用 NLP 技术更精准地提取用户搜索意图。
3. 实现**A/B 测试框架**：对比不同推荐策略的效果。

### 中期（3-6 个月）
1. 集成**深度学习模型**：使用神经网络学习更复杂的用户偏好模式。
2. 实现**实时推荐**：使用流处理技术，在用户搜索的同时更新推荐。
3. 引入**上下文信息**：考虑季节、地理位置等因素。

### 长期（6-12 个月）
1. 构建**知识图谱**：建立种子品种、特征、地区等之间的关系网络。
2. 实现**多目标优化**：平衡推荐的准确性、多样性和新颖性。
3. 开发**个性化排序**：根据用户的实时交互动态调整推荐顺序。

## 故障排查

### 问题 1：推荐结果为空
**原因**：用户没有搜索历史，或搜索历史中没有有效的关键词。
**解决**：系统会自动降级到冷启动推荐，返回热门品种。

### 问题 2：推荐结果不相关
**原因**：关键词提取不准确，或语义搜索模型效果不佳。
**解决**：
1. 检查 `SemanticSearchService` 的配置。
2. 验证 Embedding 模型是否正确加载。
3. 考虑调整推荐权重。

### 问题 3：推荐速度慢
**原因**：语义搜索涉及向量计算，可能较耗时。
**解决**：
1. 启用缓存。
2. 使用异步处理。
3. 优化向量数据库的查询。

## 监控指标

建议监控以下指标来评估推荐效果：

1. **点击率 (CTR)**：用户点击推荐品种的比例。
2. **转化率**：用户收藏或查看详情的比例。
3. **覆盖率**：推荐系统能覆盖的品种数量。
4. **多样性**：推荐结果的多样程度。
5. **新颖性**：推荐中新品种的比例。

## 相关文件

- `RecommendService.java`：推荐服务实现。
- `RecommendController.java`：推荐 API 控制器。
- `RecommendationDto.java`：推荐结果数据模型。
- `UserProfile.java`：用户画像数据模型。
