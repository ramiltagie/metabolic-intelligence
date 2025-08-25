package com.metabolicintelligence.dto.response.onboarding;

public record OnboardingResponse(
    String message,
    boolean profileCompleted,
    boolean goalCompleted,
    Double bmr,
    Double tdee
) {
}