package com.aimealplan.senseai.service;

import com.aimealplan.senseai.dto.DailySummaryDTO;
import com.aimealplan.senseai.dto.LogMealRequest;
import com.aimealplan.senseai.dto.NutrientGapDTO;
import com.aimealplan.senseai.entity.NutritionLog;
import com.aimealplan.senseai.entity.UserProfile;
import com.aimealplan.senseai.repository.NutritionRepository;
import com.aimealplan.senseai.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutritionService {

    private final NutritionRepository nutritionRepository;
    private final UserProfileRepository userProfileRepository;
    // MealPlanService will be injected later – we'll use a placeholder for now.
    // private final MealPlanService mealPlanService;

    @Transactional
    public NutritionLog logMeal(Long userId, LogMealRequest request) {
        NutritionLog log = NutritionLog.builder()
            .userId(userId)
            .loggedDate(request.date())
            .mealType(request.mealType())
            .foodName(request.foodName())
            .calories(request.calories())
            .proteinG(request.proteinG())
            .carbsG(request.carbsG())
            .fatG(request.fatG())
            .build();
        return nutritionRepository.save(log);
    }

    public DailySummaryDTO getDailySummary(Long userId, LocalDate date) {
        // Fetch user profile (will be implemented by Member 5)
        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        Integer totalCalories = nutritionRepository.sumCaloriesByUserAndDate(userId, date);
        BigDecimal totalProtein = nutritionRepository.sumProteinByUserAndDate(userId, date);
        BigDecimal totalCarbs = nutritionRepository.sumCarbsByUserAndDate(userId, date);
        BigDecimal totalFat = nutritionRepository.sumFatByUserAndDate(userId, date);

        int completionPercent = 0;
        if (profile.getCalorieTarget() != null && profile.getCalorieTarget() > 0) {
            completionPercent = (int) ((totalCalories.doubleValue() / profile.getCalorieTarget()) * 100);
            completionPercent = Math.min(completionPercent, 100);
        }

        return DailySummaryDTO.builder()
            .date(date)
            .calories(totalCalories)
            .proteinG(totalProtein)
            .carbsG(totalCarbs)
            .fatG(totalFat)
            .calorieTarget(profile.getCalorieTarget())
            .proteinTarget(profile.getProteinTarget())
            .carbsTarget(profile.getCarbsTarget())
            .fatTarget(profile.getFatTarget())
            .completionPercentage(completionPercent)
            .build();
    }

    public List<NutrientGapDTO> getNutrientGaps(Long userId, LocalDate date) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        DailySummaryDTO daily = getDailySummary(userId, date);
        List<NutrientGapDTO> gaps = new ArrayList<>();

        checkGap(gaps, "Calories",
            BigDecimal.valueOf(daily.getCalories()),
            BigDecimal.valueOf(profile.getCalorieTarget()));
        checkGap(gaps, "Protein",
            daily.getProteinG(),
            profile.getProteinTarget());
        checkGap(gaps, "Carbs",
            daily.getCarbsG(),
            profile.getCarbsTarget());
        checkGap(gaps, "Fat",
            daily.getFatG(),
            profile.getFatTarget());

        return gaps;
    }

    private void checkGap(List<NutrientGapDTO> gaps, String name, BigDecimal actual, BigDecimal target) {
        if (target == null || target.compareTo(BigDecimal.ZERO) == 0) return;

        BigDecimal percent = actual.divide(target, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        int percentInt = Math.min(percent.intValue(), 100);

        if (percentInt < 80) {
            BigDecimal gap = target.subtract(actual);
            gaps.add(NutrientGapDTO.builder()
                .nutrientName(name)
                .target(target)
                .actual(actual)
                .percentAchieved(percentInt)
                .suggestion(String.format("Add %.1fg more %s to reach your goal", gap, name.toLowerCase()))
                .build());
        }
    }

    public List<DailySummaryDTO> getWeeklySummary(Long userId, LocalDate endDate) {
        LocalDate startDate = endDate.minusDays(6);
        List<DailySummaryDTO> weekly = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            weekly.add(getDailySummary(userId, date));
        }
        return weekly;
    }

    @Transactional
    public void logFromActivePlan(Long userId) {
        // TODO: integrate with Member 1's MealPlanService once available
        // For now, just log a dummy entry to show it works
        log.info("Auto-logging from active plan for user: {}", userId);
        NutritionLog dummy = NutritionLog.builder()
            .userId(userId)
            .loggedDate(LocalDate.now())
            .mealType("lunch")
            .foodName("Auto-logged from plan")
            .calories(500)
            .proteinG(BigDecimal.valueOf(25))
            .carbsG(BigDecimal.valueOf(60))
            .fatG(BigDecimal.valueOf(15))
            .build();
        nutritionRepository.save(dummy);
    }
}