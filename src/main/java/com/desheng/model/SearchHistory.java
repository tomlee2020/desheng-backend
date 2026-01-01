package com.desheng.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 搜索历史记录实体类
 * 用于记录用户的搜索行为，便于统计热搜和推荐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("search_history")
public class SearchHistory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId; // 用户标识（可以是设备 ID 或用户 ID）

    @TableField("query")
    private String query; // 搜索关键词

    @TableField("search_type")
    private String searchType; // 搜索类型：keyword, semantic, advanced

    @TableField("result_count")
    private Integer resultCount; // 搜索结果数

    @TableField("ip_address")
    private String ipAddress; // 用户 IP 地址

    @TableField("user_agent")
    private String userAgent; // 用户代理信息

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
