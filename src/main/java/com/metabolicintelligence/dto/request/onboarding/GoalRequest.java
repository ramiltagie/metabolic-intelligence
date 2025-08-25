package com.metabolicintelligence.dto.request.onboarding;

import com.metabolicintelligence.domain.GoalType;
import com.metabolicintelligence.domain.PlanDetailLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GoalRequest(
    @NotNull
    GoalType goalType,

    @Min(20)
    @Max(400)
    Integer targetWeightKg,

    @Min(1)
    @Max(104)
    Integer targetTimelineWeeks,

    @NotNull
    PlanDetailLevel planDetailLevel
) {
}