package com.desheng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.desheng.model.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 搜索历史 Mapper
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    /**
     * 获取热搜榜单 - 按搜索频率排序
     * @param limit 返回结果数量
     * @return 热搜词列表
     */
    @Select("""
        SELECT query, COUNT(*) as search_count
        FROM search_history
        WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY query
        ORDER BY search_count DESC
        LIMIT #{limit}
        """)
    List<HotSearchDto> getHotSearches(Integer limit);

    /**
     * 获取用户搜索历史
     * @param userId 用户 ID
     * @param limit 返回结果数量
     * @return 搜索历史列表
     */
    @Select("""
        SELECT DISTINCT query
        FROM search_history
        WHERE user_id = #{userId}
        ORDER BY created_at DESC
        LIMIT #{limit}
        """)
    List<String> getUserSearchHistory(String userId, Integer limit);
}
