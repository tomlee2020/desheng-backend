package com.desheng.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedApprovalDetailsDto {

    private String id;
    private String approvalNumber;
    private String varietyName;
    private String cropName;
    private Integer approvalYear;
    private String applicant;
    private String breeder;
    private String varietySource;
    private Boolean isGMO;
    private String licenseInfo;
    private String varietyRights;
    private String approvalAuthority;
    
    private Characteristics characteristics;
    private YieldPerformance yieldPerformance;
    private CultivationTechnology cultivationTechnology;
    private ApprovalOpinion approvalOpinion;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Characteristics {
        private String detailedDescription;
        private String growthPeriod;
        private String plantHeight;
        private String resistance;
        private String qualityTraits;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YieldPerformance {
        private String summary;
        private List<YearlyData> yearlyData;
        private String comparisonData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YearlyData {
        private Integer year;
        private String location;
        private Double yield;
        private String unit;
        private String comparisonVariety;
        private Double comparisonYield;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CultivationTechnology {
        private String requirements;
        private String techniques;
        private String precautions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApprovalOpinion {
        private String opinion;
        private List<String> suitableRegions;
        private String restrictions;
    }
}