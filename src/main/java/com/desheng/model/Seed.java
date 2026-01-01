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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("seeds")
public class Seed {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("variety_name")
    private String varietyName;

    @TableField("approval_number")
    private String approvalNumber;

    @TableField("approval_year")
    private Integer approvalYear;

    @TableField("approval_region")
    private String approvalRegion;

    @TableField("crop_type")
    private String cropType;

    @TableField("company")
    private String company;

    @TableField("company_phone")
    private String companyPhone;

    @TableField("company_address")
    private String companyAddress;

    @TableField("description")
    private String description;

    @TableField("characteristics")
    private String characteristics; // JSON format

    @TableField("adaptive_regions")
    private String adaptiveRegions; // JSON format

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
