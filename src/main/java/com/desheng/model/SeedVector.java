package com.desheng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 种子向量化模型
 * 用于存储在 Redis 向量存储中的种子信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedVector {

    /**
     * 种子 ID（来自 MySQL）
     */
    private Long seedId;

    /**
     * 品种名
     */
    private String varietyName;

    /**
     * 审定编号
     */
    private String approvalNumber;

    /**
     * 作物类型
     */
    private String cropType;

    /**
     * 企业名称
     */
    private String company;

    /**
     * 综合文本内容（用于向量化）
     */
    private String content;

    /**
     * 相似度分数（搜索结果时使用）
     */
    private Double similarity;

    /**
     * 生成综合文本内容
     * 将种子的多个字段组合成一个文本用于向量化
     */
    public static String generateContent(Seed seed) {
        StringBuilder sb = new StringBuilder();
        
        if (seed.getVarietyName() != null) {
            sb.append("品种名: ").append(seed.getVarietyName()).append(" ");
        }
        
        if (seed.getCropType() != null) {
            sb.append("作物类型: ").append(seed.getCropType()).append(" ");
        }
        
        if (seed.getCompany() != null) {
            sb.append("企业: ").append(seed.getCompany()).append(" ");
        }
        
        if (seed.getApprovalNumber() != null) {
            sb.append("审定编号: ").append(seed.getApprovalNumber()).append(" ");
        }
        
        if (seed.getApprovalYear() != null) {
            sb.append("审定年份: ").append(seed.getApprovalYear()).append(" ");
        }
        
        if (seed.getApprovalRegion() != null) {
            sb.append("审定地区: ").append(seed.getApprovalRegion()).append(" ");
        }
        
        if (seed.getDescription() != null) {
            sb.append("描述: ").append(seed.getDescription()).append(" ");
        }
        
        if (seed.getCharacteristics() != null) {
            sb.append("特征: ").append(seed.getCharacteristics()).append(" ");
        }
        
        if (seed.getAdaptiveRegions() != null) {
            sb.append("适应地区: ").append(seed.getAdaptiveRegions());
        }
        
        return sb.toString();
    }
}
