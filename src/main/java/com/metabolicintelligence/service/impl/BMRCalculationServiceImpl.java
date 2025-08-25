package com.metabolicintelligence.service.impl;

import com.metabolicintelligence.domain.ActivityLevel;
import com.metabolicintelligence.domain.Gender;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.service.BMRCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BMRCalculationServiceImpl implements BMRCalculationService {
    @Override
    public BMRCalculationResponse calculateBMR(Gender gender, Integer age, Integer heightCm,
                                               Integer weightKg, Integer bodyFatPercent,
                                               ActivityLevel activityLevel) {

        double bmr;
        String formula;
        String explanation;

        if (bodyFatPercent != null && bodyFatPercent > 0) {
            bmr = calculateKatchMcArdle(weightKg, bodyFatPercent);
            formula = "Katch-McArdle";
            explanation = "BMR calculated using Katch-McArdle formula (accounts for body fat percentage)";
        } else {
            bmr = calculateMifflinStJeor(gender, age, heightCm, weightKg);
            formula = "Mifflin-St Jeor";
            explanation = "BMR calculated using Mifflin-St Jeor formula (standard method)";
        }

        double tdee = calculateTDEE(bmr, activityLevel);

        return new BMRCalculationResponse(
            Math.round(bmr * 100.0) / 100.0,
            Math.round(tdee * 100.0) / 100.0,
            formula,
            activityLevel.toString(),
            explanation
        );
    }

    @Override
    public double calculateMifflinStJeor(Gender gender, Integer age, Integer heightCm, Integer weightKg) {
        double bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age);

        if (gender == Gender.MALE) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        return bmr;
    }

    @Override
    public double calculateKatchMcArdle(Integer weightKg, Integer bodyFatPercent) {
        double leanBodyMass = weightKg * (100.0 - bodyFatPercent) / 100.0;
        return 370 + (21.6 * leanBodyMass);
    }

    @Override
    public double calculateTDEE(double bmr, ActivityLevel activityLevel) {
        double multiplier = getActivityMultiplier(activityLevel);
        return bmr * multiplier;
    }

    @Override
    public double getActivityMultiplier(ActivityLevel activityLevel) {
        return switch (activityLevel) {
            case SEDENTARY -> 1.2;
            case LIGHTLY_ACTIVE -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE -> 1.725;
            case EXTRA_ACTIVE -> 1.9;
        };
    }
}