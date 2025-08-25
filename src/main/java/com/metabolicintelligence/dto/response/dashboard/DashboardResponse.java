package com.metabolicintelligence.dto.response.dashboard;

import com.metabolicintelligence.domain.ActivityLevel;
import com.metabolicintelligence.domain.Gender;
import com.metabolicintelligence.domain.GoalType;
import com.metabolicintelligence.domain.PlanDetailLevel;

public record DashboardResponse(
    String name,
    Gender gender,
    Integer age,


    Integer heightCm,
    Integer weightKg,
    Integer bodyFatPercent,
    ActivityLevel activityLevel,


    GoalType goalType,
    Integer targetWeightKg,
    Integer targetTimelineWeeks,
    PlanDetailLevel planDetailLevel,


    Double bmr,
    Double tdee,
    String bmrFormula,


    boolean onboardingComplete,
    boolean readyForPlanGeneration
) {
}