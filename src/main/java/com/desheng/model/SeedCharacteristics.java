package com.desheng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedCharacteristics {
    private String growthPeriod;
    private String yield;
    private String diseaseResistance;
    private String qualityTraits;
}
