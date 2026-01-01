package com.desheng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐结果数据传输对象
 * 用于返回"猜你喜欢"的推荐品种
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {

    private Long id; // 种子 ID

    private String varietyName; // 品种名

    private String cropType; // 作物类型

    private String approvalNumber; // 审定编号

    private Integer approvalYear; // 审定年份

    private String approvalRegion; // 审定地区

    private String company; // 企业名

    private String description; // 描述

    private String characteristics; // 特征（JSON）

    private String reason; // 推荐理由（为什么推荐这个品种）

    private Double score; // 相似度分数（0-1）

    private String recommendationType; // 推荐类型（content-based, user-profile, trending）
}
