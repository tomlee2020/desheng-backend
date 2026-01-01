package com.desheng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.desheng.model.Seed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * Seed Mapper Interface
 * MyBatis-Plus BaseMapper 提供了基本的 CRUD 操作
 */
@Mapper
public interface SeedMapper extends BaseMapper<Seed> {

    /**
     * 自定义搜索查询 - 支持品种名、审定号、企业名的模糊搜索
     */
    @Select("SELECT * FROM seeds WHERE " +
            "LOWER(variety_name) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(approval_number) LIKE LOWER(CONCAT('%', #{keyword}, '%')) OR " +
            "LOWER(company) LIKE LOWER(CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY approval_year DESC")
    IPage<Seed> searchByKeyword(Page<Seed> page, @Param("keyword") String keyword);

    /**
     * 根据作物类型和其他条件的组合查询
     */
    @Select("<script>" +
            "SELECT * FROM seeds WHERE 1=1 " +
            "<if test='cropType != null'> AND crop_type = #{cropType}</if> " +
            "<if test='approvalRegion != null'> AND approval_region LIKE CONCAT('%', #{approvalRegion}, '%')</if> " +
            "<if test='startYear != null'> AND approval_year >= #{startYear}</if> " +
            "<if test='endYear != null'> AND approval_year <= #{endYear}</if> " +
            "<if test='company != null'> AND company LIKE CONCAT('%', #{company}, '%')</if> " +
            "ORDER BY approval_year DESC" +
            "</script>")
    IPage<Seed> findByMultipleFilters(Page<Seed> page,
                                      @Param("cropType") String cropType,
                                      @Param("approvalRegion") String approvalRegion,
                                      @Param("startYear") Integer startYear,
                                      @Param("endYear") Integer endYear,
                                      @Param("company") String company);
}
