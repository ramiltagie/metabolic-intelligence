package com.metabolicintelligence.service;

import com.metabolicintelligence.dto.request.onboarding.GoalRequest;
import com.metabolicintelligence.dto.request.onboarding.ProfileRequest;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.dto.response.onboarding.OnboardingResponse;

public interface OnboardingService {
    OnboardingResponse createProfile(Integer userId, ProfileRequest request);

    OnboardingResponse createGoal(Integer userId, GoalRequest request);

    OnboardingResponse getOnboardingStatus(Integer userId);

    BMRCalculationResponse calculateUserMetabolics(Integer userId);

    boolean isOnboardingComplete(Integer userId);
}