package com.metabolicintelligence.dto.response.onboarding;

public record BMRCalculationResponse(
    Double bmr,
    Double tdee,
    String formula,
    String activityLevel,
    String explanation
) {
}