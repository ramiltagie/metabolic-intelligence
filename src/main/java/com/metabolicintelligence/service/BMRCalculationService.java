package com.metabolicintelligence.service;

import com.metabolicintelligence.domain.ActivityLevel;
import com.metabolicintelligence.domain.Gender;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;

public interface BMRCalculationService {
    BMRCalculationResponse calculateBMR(Gender gender, Integer age, Integer heightCm,
                                        Integer weightKg, Integer bodyFatPercent,
                                        ActivityLevel activityLevel);

    double calculateMifflinStJeor(Gender gender, Integer age, Integer heightCm, Integer weightKg);

    double calculateKatchMcArdle(Integer weightKg, Integer bodyFatPercent);

    double calculateTDEE(double bmr, ActivityLevel activityLevel);

    double getActivityMultiplier(ActivityLevel activityLevel);
}