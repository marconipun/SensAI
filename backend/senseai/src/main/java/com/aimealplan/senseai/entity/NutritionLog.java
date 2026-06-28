package com.aimealplan.senseai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "logged_date", nullable = false)
    private LocalDate loggedDate;

    @Column(name = "meal_type", length = 20)
    private String mealType; // breakfast, lunch, dinner, snack

    @Column(name = "food_name", length = 200)
    private String foodName;

    private Integer calories;

    @Column(name = "protein_g", precision = 6, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 6, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 6, scale = 2)
    private BigDecimal fatG;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
