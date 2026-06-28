package com.aimealplan.senseai.service;

import com.aimealplan.senseai.dto.DailySummaryDTO;
import com.aimealplan.senseai.dto.LogMealRequest;
import com.aimealplan.senseai.entity.NutritionLog;
import com.aimealplan.senseai.entity.UserProfile;
import com.aimealplan.senseai.repository.NutritionRepository;
import com.aimealplan.senseai.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionServiceTest {

    @Mock private NutritionRepository nutritionRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @InjectMocks private NutritionService nutritionService;

    @Test
    void shouldLogMeal() {
        Long userId = 1L;
        LogMealRequest request = new LogMealRequest(
            LocalDate.now(), "lunch", "Chicken Salad", 450,
            BigDecimal.valueOf(30), BigDecimal.valueOf(10), BigDecimal.valueOf(15)
        );
        NutritionLog saved = NutritionLog.builder().id(1L).userId(userId).calories(450).build();
        when(nutritionRepository.save(any(NutritionLog.class))).thenReturn(saved);

        NutritionLog result = nutritionService.logMeal(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.getCalories()).isEqualTo(450);
        verify(nutritionRepository, times(1)).save(any(NutritionLog.class));
    }

    @Test
    void shouldReturnDailySummary() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        UserProfile profile = UserProfile.builder()
            .userId(userId)
            .calorieTarget(2000)
            .proteinTarget(BigDecimal.valueOf(150))
            .carbsTarget(BigDecimal.valueOf(250))
            .fatTarget(BigDecimal.valueOf(65))
            .build();
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(nutritionRepository.sumCaloriesByUserAndDate(userId, today)).thenReturn(1500);
        when(nutritionRepository.sumProteinByUserAndDate(userId, today)).thenReturn(BigDecimal.valueOf(100));
        when(nutritionRepository.sumCarbsByUserAndDate(userId, today)).thenReturn(BigDecimal.valueOf(200));
        when(nutritionRepository.sumFatByUserAndDate(userId, today)).thenReturn(BigDecimal.valueOf(50));

        DailySummaryDTO result = nutritionService.getDailySummary(userId, today);

        assertThat(result.getCalories()).isEqualTo(1500);
        assertThat(result.getCompletionPercentage()).isEqualTo(75); // 1500/2000
    }
}