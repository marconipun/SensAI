package com.aimealplan.senseai.repository;

import com.aimealplan.senseai.entity.NutritionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface NutritionRepository extends JpaRepository<NutritionLog, Long> {

    // Weekly range
    List<NutritionLog> findByUserIdAndLoggedDateBetween(Long userId, LocalDate start, LocalDate end);

    // Sum queries for daily totals
    @Query("SELECT COALESCE(SUM(n.calories), 0) FROM NutritionLog n WHERE n.userId = :userId AND n.loggedDate = :date")
    Integer sumCaloriesByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(n.proteinG), 0) FROM NutritionLog n WHERE n.userId = :userId AND n.loggedDate = :date")
    BigDecimal sumProteinByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(n.carbsG), 0) FROM NutritionLog n WHERE n.userId = :userId AND n.loggedDate = :date")
    BigDecimal sumCarbsByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(n.fatG), 0) FROM NutritionLog n WHERE n.userId = :userId AND n.loggedDate = :date")
    BigDecimal sumFatByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}