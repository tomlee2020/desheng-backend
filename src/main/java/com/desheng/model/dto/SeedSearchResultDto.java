package com.desheng.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedSearchResultDto {

    private String id;
    private String approvalNumber;
    private String varietyName;
    private String cropName;
    private Integer approvalYear;
    private String applicant;
    private String breeder;
    private Boolean isGMO;
    private String approvalAuthority;
    private List<String> suitableRegions;
}