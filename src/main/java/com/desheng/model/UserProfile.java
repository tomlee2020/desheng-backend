package com.desheng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 用户画像类
 * 基于用户的搜索历史和行为生成的用户特征
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    private String userId; // 用户 ID

    private List<String> searchKeywords; // 搜索关键词列表

    private Map<String, Integer> cropTypeFrequency; // 作物类型频率分布

    private Map<String, Integer> regionFrequency; // 地区频率分布

    private String preferredCropType; // 最偏好的作物类型

    private String preferredRegion; // 最偏好的地区

    private Integer totalSearchCount; // 总搜索次数

    private Long lastSearchTime; // 最后搜索时间

    private String userProfileVector; // 用户画像向量（JSON 格式）
}
