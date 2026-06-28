package com.aimealplan.senseai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;
    @Column(name = "calorie_target")
    private Integer calorieTarget;
    @Column(name = "protein_target")
    private BigDecimal proteinTarget;
    @Column(name = "carbs_target")
    private BigDecimal carbsTarget;
    @Column(name = "fat_target")
    private BigDecimal fatTarget;
}