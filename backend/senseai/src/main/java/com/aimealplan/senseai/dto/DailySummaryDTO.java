package com.aimealplan.senseai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryDTO {
    private LocalDate date;
    private Integer calories;
    private BigDecimal proteinG;
    private BigDecimal carbsG;
    private BigDecimal fatG;
    private Integer calorieTarget;
    private BigDecimal proteinTarget;
    private BigDecimal carbsTarget;
    private BigDecimal fatTarget;
    private Integer completionPercentage;
}