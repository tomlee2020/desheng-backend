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

/**
 * Elasticsearch Document for Seed
 * 用于 Elasticsearch 索引的种子文档
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "seeds", createIndex = true)
public class SeedDocument {

    @Id
    private Long id;

    /**
     * 品种名 - 使用 text 类型支持全文搜索和分词
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String varietyName;

    /**
     * 品种名拼音 - 用于拼音搜索
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String varietyNamePinyin;

    /**
     * 品种名拼音首字母 - 用于简拼搜索
     */
    @Field(type = FieldType.Keyword)
    private String varietyNamePinyinShort;

    /**
     * 审定编号
     */
    @Field(type = FieldType.Keyword)
    private String approvalNumber;

    /**
     * 审定年份
     */
    @Field(type = FieldType.Integer)
    private Integer approvalYear;

    /**
     * 审定地区
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String approvalRegion;

    /**
     * 作物类型
     */
    @Field(type = FieldType.Keyword)
    private String cropType;

    /**
     * 企业名称 - 支持全文搜索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String company;

    /**
     * 企业名称拼音
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String companyPinyin;

    /**
     * 企业电话
     */
    @Field(type = FieldType.Keyword)
    private String companyPhone;

    /**
     * 企业地址
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String companyAddress;

    /**
     * 详细描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /**
     * 特征信息（JSON 格式）
     */
    @Field(type = FieldType.Text)
    private String characteristics;

    /**
     * 适应地区（JSON 格式）
     */
    @Field(type = FieldType.Text)
    private String adaptiveRegions;

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
}
