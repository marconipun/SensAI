package com.aimealplan.senseai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutrientGapDTO {
    private String nutrientName;
    private BigDecimal target;
    private BigDecimal actual;
    private Integer percentAchieved;
    private String suggestion; // e.g., "Add 20g more protein"
}