# 后端API接口规范 - 种子审定详情功能

## 概述

本文档定义了支持种子审定详情功能的后端API接口规范，包括高级搜索、筛选、审定详情查询等功能。

## 基础配置

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **字符编码**: UTF-8

---

## 1. 种子审定详情接口

### 1.1 获取种子审定详情

**接口**: `GET /seeds/{id}/approval-details`

**描述**: 获取指定种子的完整审定详情信息

**路径参数**:
- `id` (string/number): 种子ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "12345",
    "approvalNumber": "国审稻20210001",
    "varietyName": "华优1号",
    "cropName": "水稻",
    "approvalYear": 2021,
    "applicant": "中国农业科学院作物科学研究所",
    "breeder": "张三, 李四",
    "varietySource": "华A × 优1",
    "isGMO": false,
    "licenseInfo": "生产许可证号：(2021)农种许字第001号",
    "varietyRights": "品种权号：CNA20210001.1",
    "approvalAuthority": "国家农作物品种审定委员会",
    "characteristics": {
      "detailedDescription": "该品种属籼型三系杂交水稻，全生育期135天...",
      "growthPeriod": "135天",
      "plantHeight": "110厘米",
      "resistance": "中抗稻瘟病，抗白叶枯病",
      "qualityTraits": "米质优良，整精米率65%，垩白粒率12%"
    },
    "yieldPerformance": {
      "summary": "两年区域试验平均亩产620.5公斤，比对照增产8.5%",
      "yearlyData": [
        {
          "year": 2019,
          "location": "湖南长沙",
          "yield": 615.2,
          "unit": "公斤/亩",
          "comparisonVariety": "对照品种",
          "comparisonYield": 570.3
        },
        {
          "year": 2020,
          "location": "湖南长沙",
          "yield": 625.8,
          "unit": "公斤/亩",
          "comparisonVariety": "对照品种",
          "comparisonYield": 575.6
        }
      ],
      "comparisonData": "比对照品种增产8.5%"
    },
    "cultivationTechnology": {
      "requirements": "适宜在长江中下游稻区种植",
      "techniques": "适时播种，培育壮秧；合理密植，每亩1.5-1.8万穴",
      "precautions": "注意防治稻瘟病和纹枯病"
    },
    "approvalOpinion": {
      "opinion": "该品种符合国家水稻品种审定标准，通过审定",
      "suitableRegions": ["湖南", "湖北", "江西", "安徽"],
      "restrictions": "不适宜在稻瘟病重发区种植"
    },
    "createdAt": "2021-03-15T08:30:00Z",
    "updatedAt": "2021-03-15T08:30:00Z",
    "version": 1
  }
}
```

---

## 2. 高级搜索和筛选接口

### 2.1 高级搜索（支持多条件筛选）

**接口**: `POST /seeds/search/advanced`

**描述**: 支持多条件组合的高级搜索，包括审定编号、品种名称、申请单位、作物种类、审定年份、审定单位等

**请求体**:
```json
{
  "keyword": "水稻",
  "approvalNumber": "国审稻20210001",
  "varietyName": "华优1号",
  "applicant": "中国农业科学院",
  "breeder": "张三",
  "cropName": "水稻",
  "approvalYear": 2021,
  "approvalYearRange": [2020, 2023],
  "isGMO": false,
  "approvalAuthority": "国家农作物品种审定委员会",
  "suitableRegion": "湖南",
  "page": 1,
  "pageSize": 20
}
```

**请求参数说明**:
- `keyword` (string, 可选): 关键词，搜索品种名称、审定编号、公司名称
- `approvalNumber` (string, 可选): 审定编号（精确匹配）
- `varietyName` (string, 可选): 品种名称（支持模糊匹配）
- `applicant` (string, 可选): 申请单位（支持模糊匹配）
- `breeder` (string, 可选): 育种者（支持模糊匹配）
- `cropName` (string, 可选): 作物名称（水稻、玉米、小麦、棉花、花生、大豆）
- `approvalYear` (number, 可选): 审定年份（精确匹配）
- `approvalYearRange` (array, 可选): 审定年份范围 [起始年份, 结束年份]
- `isGMO` (boolean, 可选): 是否转基因
- `approvalAuthority` (string, 可选): 审定单位（国家或各省份审定委员会）
- `suitableRegion` (string, 可选): 适宜种植区域
- `page` (number, 可选): 页码，默认1
- `pageSize` (number, 可选): 每页数量，默认20

**前端高级筛选支持的6个主要筛选条件**:
1. **审定编号** - 文本输入，精确匹配
2. **品种名称** - 文本输入，模糊匹配
3. **申请单位** - 文本输入，模糊匹配
4. **作物种类** - 按钮选择（水稻、玉米、小麦、棉花、花生、大豆）
5. **审定年份** - 按钮选择（最近10年）
6. **审定单位** - 按钮选择（国家 + 32个省级审定委员会）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "12345",
        "approvalNumber": "国审稻20210001",
        "varietyName": "华优1号",
        "cropName": "水稻",
        "approvalYear": 2021,
        "applicant": "中国农业科学院作物科学研究所",
        "breeder": "张三, 李四",
        "isGMO": false,
        "approvalAuthority": "国家农作物品种审定委员会",
        "suitableRegions": ["湖南", "湖北", "江西", "安徽"]
      }
    ],
    "total": 150,
    "page": 1,
    "pageSize": 20,
    "totalPages": 8
  }
}
```

### 2.2 按申请者搜索

**接口**: `GET /seeds/search/by-applicant`

**描述**: 按申请单位搜索种子品种

**查询参数**:
- `applicant` (string, 必需): 申请单位名称（支持模糊匹配）
- `page` (number, 可选): 页码，默认1
- `pageSize` (number, 可选): 每页数量，默认20

**示例**: `GET /seeds/search/by-applicant?applicant=中国农业科学院&page=1&pageSize=20`

**响应格式**: 同高级搜索响应

### 2.3 按育种者搜索

**接口**: `GET /seeds/search/by-breeder`

**描述**: 按育种者搜索种子品种

**查询参数**:
- `breeder` (string, 必需): 育种者姓名（支持模糊匹配）
- `page` (number, 可选): 页码，默认1
- `pageSize` (number, 可选): 每页数量，默认20

**示例**: `GET /seeds/search/by-breeder?breeder=张三&page=1&pageSize=20`

**响应格式**: 同高级搜索响应

### 2.4 按审定编号精确搜索

**接口**: `GET /seeds/search/by-approval-number`

**描述**: 按审定编号进行精确匹配搜索

**查询参数**:
- `approvalNumber` (string, 必需): 审定编号（精确匹配）

**示例**: `GET /seeds/search/by-approval-number?approvalNumber=国审稻20210001`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "12345",
    "approvalNumber": "国审稻20210001",
    "varietyName": "华优1号",
    "cropName": "水稻",
    "approvalYear": 2021,
    "applicant": "中国农业科学院作物科学研究所",
    "breeder": "张三, 李四",
    "isGMO": false
  }
}
```

### 2.5 转基因品种筛选

**接口**: `GET /seeds/search/gmo`

**描述**: 筛选转基因或非转基因品种

**查询参数**:
- `isGMO` (boolean, 必需): true=转基因品种, false=非转基因品种
- `page` (number, 可选): 页码，默认1
- `pageSize` (number, 可选): 每页数量，默认20

**示例**: `GET /seeds/search/gmo?isGMO=false&page=1&pageSize=20`

**响应格式**: 同高级搜索响应

---

## 3. 辅助查询接口

### 3.1 获取申请单位列表

**接口**: `GET /seeds/applicants`

**描述**: 获取所有申请单位列表（用于筛选下拉框）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "中国农业科学院作物科学研究所",
    "湖南省农业科学院",
    "江西省农业科学院",
    "安徽省农业科学院"
  ]
}
```

### 3.2 获取育种者列表

**接口**: `GET /seeds/breeders`

**描述**: 获取所有育种者列表（用于筛选下拉框）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "张三",
    "李四",
    "王五",
    "赵六"
  ]
}
```

### 3.3 获取审定单位列表

**接口**: `GET /seeds/approval-authorities`

**描述**: 获取所有审定单位列表（用于筛选下拉框）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "国家农作物品种审定委员会",
    "湖南省农作物品种审定委员会",
    "湖北省农作物品种审定委员会"
  ]
}
```

### 3.4 获取审定编号建议

**接口**: `GET /seeds/approval-number-suggestions`

**描述**: 根据输入获取审定编号建议（用于自动完成）

**查询参数**:
- `query` (string, 必需): 搜索关键词

**示例**: `GET /seeds/approval-number-suggestions?query=国审稻2021`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "国审稻20210001",
    "国审稻20210002",
    "国审稻20210003"
  ]
}
```

---

## 4. 数据库字段映射

### 4.1 种子审定详情表结构

后端数据库应包含以下字段：

```sql
CREATE TABLE seed_approval_details (
  id VARCHAR(50) PRIMARY KEY,
  approval_number VARCHAR(50) UNIQUE NOT NULL,
  variety_name VARCHAR(200) NOT NULL,
  crop_name VARCHAR(100) NOT NULL,
  approval_year INT NOT NULL,
  
  -- 申请信息
  applicant VARCHAR(500) NOT NULL,
  breeder VARCHAR(500) NOT NULL,
  variety_source TEXT,
  
  -- 法规信息
  is_gmo BOOLEAN DEFAULT FALSE,
  license_info TEXT,
  variety_rights TEXT,
  approval_authority VARCHAR(200) NOT NULL,
  
  -- 特征特性
  detailed_description TEXT,
  growth_period VARCHAR(100),
  plant_height VARCHAR(50),
  resistance TEXT,
  quality_traits TEXT,
  
  -- 产量表现
  yield_summary TEXT,
  comparison_data TEXT,
  
  -- 栽培技术
  cultivation_requirements TEXT,
  cultivation_techniques TEXT,
  cultivation_precautions TEXT,
  
  -- 审定意见
  approval_opinion TEXT,
  suitable_regions JSON,
  planting_restrictions TEXT,
  
  -- 元数据
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  version INT DEFAULT 1,
  
  -- 索引
  INDEX idx_approval_number (approval_number),
  INDEX idx_variety_name (variety_name),
  INDEX idx_applicant (applicant),
  INDEX idx_breeder (breeder),
  INDEX idx_crop_name (crop_name),
  INDEX idx_approval_year (approval_year),
  INDEX idx_is_gmo (is_gmo)
);
```

### 4.2 产量数据表结构

```sql
CREATE TABLE yield_performance_data (
  id INT AUTO_INCREMENT PRIMARY KEY,
  approval_id VARCHAR(50) NOT NULL,
  year INT NOT NULL,
  location VARCHAR(200) NOT NULL,
  yield_value DECIMAL(10,2) NOT NULL,
  yield_unit VARCHAR(20) NOT NULL,
  comparison_variety VARCHAR(200),
  comparison_yield DECIMAL(10,2),
  
  FOREIGN KEY (approval_id) REFERENCES seed_approval_details(id),
  INDEX idx_approval_year (approval_id, year),
  INDEX idx_location (location)
);
```

---

## 5. 错误响应格式

所有接口在出错时应返回统一的错误格式：

```json
{
  "code": 400,
  "message": "Invalid request parameters",
  "error": "approvalNumber format is invalid",
  "details": {
    "field": "approvalNumber",
    "expectedFormat": "2-4 letters followed by 4-8 digits"
  }
}
```

**常见错误码**:
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 6. 性能优化建议

### 6.1 数据库索引
- 为常用搜索字段创建索引（approval_number, applicant, breeder, crop_name, approval_year, is_gmo）
- 为全文搜索字段创建全文索引（variety_name, detailed_description）

### 6.2 缓存策略
- 缓存申请单位、育种者、审定单位列表（更新频率低）
- 缓存热门搜索结果（TTL: 5分钟）
- 使用Redis缓存审定详情（TTL: 1小时）

### 6.3 分页优化
- 默认每页20条记录
- 最大每页100条记录
- 使用游标分页优化大数据量查询

---

## 7. 实现优先级

### 第一阶段（核心功能）
1. ✅ 获取种子审定详情 (`GET /seeds/{id}/approval-details`)
2. ✅ 高级搜索接口 (`POST /seeds/search/advanced`)
   - ✅ 支持审定编号精确匹配
   - ✅ 支持品种名称模糊匹配
   - ✅ 支持申请单位模糊匹配
   - ✅ 支持作物种类筛选（水稻、玉米、小麦、棉花、花生、大豆）
   - ✅ 支持审定年份筛选
   - ✅ 支持审定单位筛选（国家 + 32个省级审定委员会）
3. ✅ 按审定编号精确搜索 (`GET /seeds/search/by-approval-number`)

### 第二阶段（辅助功能）
4. 按申请者搜索 (`GET /seeds/search/by-applicant`)
5. 按育种者搜索 (`GET /seeds/search/by-breeder`)
6. 转基因品种筛选 (`GET /seeds/search/gmo`)

### 第三阶段（优化功能）
7. 获取申请单位列表 (`GET /seeds/applicants`)
8. 获取育种者列表 (`GET /seeds/breeders`)
9. 获取审定单位列表 (`GET /seeds/approval-authorities`)
10. 获取审定编号建议 (`GET /seeds/approval-number-suggestions`)

---

## 8. 前端高级筛选实现说明

### 8.1 筛选条件

前端已实现的6个主要筛选条件：

1. **审定编号** (`approvalNumber`)
   - 类型：文本输入框
   - 匹配方式：精确匹配
   - 示例：`国审稻20210001`

2. **品种名称** (`varietyName`)
   - 类型：文本输入框
   - 匹配方式：模糊匹配
   - 示例：`华优`

3. **申请单位** (`applicant`)
   - 类型：文本输入框
   - 匹配方式：模糊匹配
   - 示例：`中国农业科学院`

4. **作物种类** (`cropName`)
   - 类型：按钮选择（单选）
   - 选项：水稻、玉米、小麦、棉花、花生、大豆
   - 匹配方式：精确匹配

5. **审定年份** (`approvalYear`)
   - 类型：按钮选择（单选）
   - 选项：最近10年（动态生成）
   - 匹配方式：精确匹配

6. **审定单位** (`approvalAuthority`)
   - 类型：按钮选择（单选）
   - 选项：国家农作物品种审定委员会 + 32个省级审定委员会
   - 匹配方式：包含匹配
   - 显示优化：按钮文本去掉"农作物品种审定委员会"后缀

### 8.2 筛选逻辑

- 所有筛选条件支持组合使用
- 使用单次遍历优化性能
- 支持中文locale排序
- 筛选结果支持排序（按审定年份、公司、品种名称、审定编号）

### 8.3 Mock数据

前端已提供完整的Mock数据用于开发测试：
- 8个种子品种（水稻3个、玉米1个、小麦1个、大豆1个、棉花1个、花生1个）
- 覆盖2021-2024年审定年份
- 包含国家级和省级审定单位
- 所有品种均为非转基因

---

## 9. 前端调用示例

```typescript
// 高级搜索 - 使用新的筛选条件
const searchResults = await fetch('/api/seeds/search/advanced', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    approvalNumber: '国审稻20210001',  // 审定编号（精确匹配）
    varietyName: '华优',                // 品种名称（模糊匹配）
    applicant: '中国农业科学院',        // 申请单位（模糊匹配）
    cropName: '水稻',                   // 作物种类
    approvalYear: 2021,                 // 审定年份
    approvalAuthority: '国家农作物品种审定委员会',  // 审定单位
    page: 1,
    pageSize: 20
  })
});

// 按审定编号搜索
const seed = await fetch('/api/seeds/search/by-approval-number?approvalNumber=国审稻20210001');

// 获取审定详情
const details = await fetch('/api/seeds/12345/approval-details');
```

---

## 10. 总结

这套API接口设计支持：
- ✅ 审定编号精确匹配搜索
- ✅ 按申请者、育种者搜索（模糊匹配）
- ✅ 转基因品种筛选
- ✅ 多条件组合筛选
- ✅ 性能优化（索引、缓存、分页）
- ✅ 完整的审定详情数据结构

前端已经实现了相应的筛选逻辑，后端只需按照此规范实现接口即可无缝对接。
