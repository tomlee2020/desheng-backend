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
public class AdvancedSearchRequest {

    private String keyword;
    private String approvalNumber;
    private String varietyName;
    private String applicant;
    private String breeder;
    private String cropName;
    private Integer approvalYear;
    private List<Integer> approvalYearRange;
    private Boolean isGMO;
    private String approvalAuthority;
    private String suitableRegion;
    
    @Builder.Default
    private Integer page = 1;
    
    @Builder.Default
    private Integer pageSize = 20;
}