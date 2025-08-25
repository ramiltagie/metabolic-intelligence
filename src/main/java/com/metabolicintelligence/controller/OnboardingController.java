package com.metabolicintelligence.controller;

import com.metabolicintelligence.dto.request.onboarding.GoalRequest;
import com.metabolicintelligence.dto.request.onboarding.ProfileRequest;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.dto.response.onboarding.OnboardingResponse;
import com.metabolicintelligence.service.OnboardingService;
import com.metabolicintelligence.service.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
@Validated
@RequiredArgsConstructor
public class OnboardingController {
    private final OnboardingService onboardingService;
    private final UserAuthenticationService userAuthenticationService;

    @GetMapping("/status")
    public ResponseEntity<OnboardingResponse> getOnboardingStatus() {
        Integer userId = userAuthenticationService.getCurrentUserId();
        OnboardingResponse response = onboardingService.getOnboardingStatus(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<OnboardingResponse> createProfile(
        @RequestBody @Valid ProfileRequest request) {

        Integer userId = userAuthenticationService.getCurrentUserId();
        OnboardingResponse response = onboardingService.createProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/goals")
    public ResponseEntity<OnboardingResponse> createGoals(
        @RequestBody @Valid GoalRequest request) {

        Integer userId = userAuthenticationService.getCurrentUserId();
        OnboardingResponse response = onboardingService.createGoal(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calculate-bmr")
    public ResponseEntity<BMRCalculationResponse> calculateBMR() {
        Integer userId = userAuthenticationService.getCurrentUserId();
        BMRCalculationResponse response = onboardingService.calculateUserMetabolics(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/complete")
    public ResponseEntity<Boolean> isOnboardingComplete() {
        Integer userId = userAuthenticationService.getCurrentUserId();
        boolean isComplete = onboardingService.isOnboardingComplete(userId);
        return ResponseEntity.ok(isComplete);
    }

}