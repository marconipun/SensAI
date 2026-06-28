package com.aimealplan.senseai.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LogMealRequest(
    @NotNull LocalDate date,
    @NotNull String mealType,
    String foodName,
    Integer calories,
    BigDecimal proteinG,
    BigDecimal carbsG,
    BigDecimal fatG
) {}