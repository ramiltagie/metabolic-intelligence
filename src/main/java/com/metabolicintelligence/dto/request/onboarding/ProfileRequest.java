package com.metabolicintelligence.dto.request.onboarding;

import com.metabolicintelligence.domain.ActivityLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProfileRequest(
    @NotNull
    @Min(80)
    @Max(250)
    Integer heightCm,

    @NotNull
    @Min(20)
    @Max(400)
    Integer weightKg,

    @Min(10)
    @Max(100)
    Integer bodyFatPercent,

    @NotNull
    ActivityLevel activityLevel,

    @Min(0)
    @Max(24)
    Integer sleepHours,

    String dietNotes,

    Boolean smoking,

    Boolean alcohol
) {
}