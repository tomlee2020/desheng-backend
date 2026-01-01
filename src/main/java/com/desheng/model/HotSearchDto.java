package com.desheng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热搜数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotSearchDto {

    private String query; // 搜索词

    private Long searchCount; // 搜索次数

    private Integer rank; // 排名（可选）
}
