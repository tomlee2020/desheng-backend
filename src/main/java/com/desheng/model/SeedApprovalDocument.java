package com.desheng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Elasticsearch Document for Seed Approval Details
 * 种子审定详情的ES文档
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "seed_approval_details", createIndex = true)
public class SeedApprovalDocument {

    @Id
    private String id;

    /**
     * 审定编号 - 精确匹配
     */
    @Field(type = FieldType.Keyword)
    private String approvalNumber;

    /**
     * 品种名称 - 支持全文搜索和模糊匹配
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String varietyName;

    /**
     * 品种名称拼音
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String varietyNamePinyin;

    /**
     * 品种名称拼音首字母
     */
    @Field(type = FieldType.Keyword)
    private String varietyNamePinyinShort;

    /**
     * 作物名称
     */
    @Field(type = FieldType.Keyword)
    private String cropName;

    /**
     * 审定年份
     */
    @Field(type = FieldType.Integer)
    private Integer approvalYear;

    /**
     * 申请单位 - 支持模糊匹配
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String applicant;

    /**
     * 申请单位拼音
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String applicantPinyin;

    /**
     * 育种者 - 支持模糊匹配
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String breeder;

    /**
     * 育种者拼音
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String breederPinyin;

    /**
     * 品种来源
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String varietySource;

    /**
     * 是否转基因
     */
    @Field(type = FieldType.Boolean)
    private Boolean isGMO;

    /**
     * 生产许可证信息
     */
    @Field(type = FieldType.Text)
    private String licenseInfo;

    /**
     * 品种权信息
     */
    @Field(type = FieldType.Text)
    private String varietyRights;

    /**
     * 审定单位 - 支持包含匹配
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String approvalAuthority;

    /**
     * 详细描述 - 全文搜索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String detailedDescription;

    /**
     * 生育期
     */
    @Field(type = FieldType.Keyword)
    private String growthPeriod;

    /**
     * 株高
     */
    @Field(type = FieldType.Keyword)
    private String plantHeight;

    /**
     * 抗性
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String resistance;

    /**
     * 品质性状
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String qualityTraits;

    /**
     * 产量表现摘要
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String yieldSummary;

    /**
     * 对比数据
     */
    @Field(type = FieldType.Text)
    private String comparisonData;

    /**
     * 栽培要求
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String cultivationRequirements;

    /**
     * 栽培技术
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String cultivationTechniques;

    /**
     * 栽培注意事项
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String cultivationPrecautions;

    /**
     * 审定意见
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String approvalOpinion;

    /**
     * 适宜地区 - 支持地区搜索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private List<String> suitableRegions;

    /**
     * 种植限制
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String plantingRestrictions;

    /**
     * 产量数据 - 嵌套对象
     */
    @Field(type = FieldType.Nested)
    private List<YieldData> yieldData;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    /**
     * 版本号
     */
    @Field(type = FieldType.Integer)
    private Integer version;

    /**
     * 产量数据内嵌类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YieldData {
        @Field(type = FieldType.Integer)
        private Integer year;

        @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
        private String location;

        @Field(type = FieldType.Double)
        private Double yieldValue;

        @Field(type = FieldType.Keyword)
        private String yieldUnit;

        @Field(type = FieldType.Text)
        private String comparisonVariety;

        @Field(type = FieldType.Double)
        private Double comparisonYield;
    }
}