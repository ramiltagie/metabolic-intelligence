package com.metabolicintelligence.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Min(80)
    @Max(250)
    @Column(name = "height_cm", nullable = false)
    private Integer heightCm;

    @Min(20)
    @Max(400)
    @Column(name = "weight_kg", nullable = false)
    private Integer weightKg;

    @Min(10)
    @Max(100)
    @Column(name = "body_fat_percent")
    private Integer bodyFatPercent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false, length = 32)
    private ActivityLevel activityLevel;

    @Min(0)
    @Max(24)
    @Column(name = "sleep_hours")
    private Integer sleepHours;

    @Column(name = "diet_notes")
    private String dietNotes;

    @Column(name = "smoking")
    private Boolean smoking;

    @Column(name = "alcohol")
    private Boolean alcohol;
}
