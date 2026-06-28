package com.aimealplan.senseai.controller;

import com.aimealplan.senseai.dto.DailySummaryDTO;
import com.aimealplan.senseai.dto.LogMealRequest;
import com.aimealplan.senseai.dto.NutrientGapDTO;
import com.aimealplan.senseai.entity.NutritionLog;
import com.aimealplan.senseai.service.NutritionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    @PostMapping("/log")
    public ResponseEntity<NutritionLog> logMeal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LogMealRequest request) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(nutritionService.logMeal(userId, request));
    }

    @GetMapping("/daily")
    public ResponseEntity<DailySummaryDTO> getDailySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = extractUserId(userDetails);
        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(nutritionService.getDailySummary(userId, date));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<DailySummaryDTO>> getWeeklySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = extractUserId(userDetails);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(nutritionService.getWeeklySummary(userId, endDate));
    }

    @GetMapping("/gaps")
    public ResponseEntity<List<NutrientGapDTO>> getNutrientGaps(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = extractUserId(userDetails);
        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(nutritionService.getNutrientGaps(userId, date));
    }

    @PostMapping("/log-from-plan")
    public ResponseEntity<Void> logFromActivePlan(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        nutritionService.logFromActivePlan(userId);
        return ResponseEntity.ok().build();
    }

    // Temporary helper – will be replaced by proper auth extraction from Member 5
    private Long extractUserId(UserDetails userDetails) {
        // For now, return a fixed ID (1) because we don't have real users yet.
        // Later, you'll get it from the User entity.
        return 1L;
    }
}
