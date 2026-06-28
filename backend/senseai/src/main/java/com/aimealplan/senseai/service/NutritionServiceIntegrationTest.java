package com.aimealplan.senseai.service;

import com.aimealplan.senseai.entity.NutritionLog;
import com.aimealplan.senseai.repository.NutritionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NutritionServiceIntegrationTest {

    @Autowired private NutritionService nutritionService;
    @Autowired private NutritionRepository nutritionRepository;

    @Test
    void shouldPersistAndRetrieve() {
        NutritionLog log = NutritionLog.builder()
            .userId(1L)
            .loggedDate(LocalDate.now())
            .mealType("breakfast")
            .foodName("Oatmeal")
            .calories(300)
            .proteinG(BigDecimal.valueOf(10))
            .carbsG(BigDecimal.valueOf(40))
            .fatG(BigDecimal.valueOf(5))
            .build();
        nutritionRepository.save(log);

        var found = nutritionRepository.findByUserIdAndLoggedDateBetween(1L, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFoodName()).isEqualTo("Oatmeal");
    }
}