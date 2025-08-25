package com.metabolicintelligence.dto.response.plan;

import java.time.LocalDateTime;

public record TransformationPlanResponse(
    String weeklyPlan,
    String calorieGoals,
    String macroBreakdown,
    String workoutRecommendations,
    String mealSuggestions,
    String timelineTargets,
    LocalDateTime generatedAt
) {
}