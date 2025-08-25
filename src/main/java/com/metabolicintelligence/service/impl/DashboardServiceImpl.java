package com.metabolicintelligence.service.impl;

import com.metabolicintelligence.domain.Goal;
import com.metabolicintelligence.domain.User;
import com.metabolicintelligence.domain.UserProfile;
import com.metabolicintelligence.dto.response.dashboard.DashboardResponse;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.repository.GoalRepository;
import com.metabolicintelligence.repository.UserProfileRepository;
import com.metabolicintelligence.repository.UserRepository;
import com.metabolicintelligence.service.BMRCalculationService;
import com.metabolicintelligence.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.metabolicintelligence.util.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final GoalRepository goalRepository;
    private final BMRCalculationService bmrCalculationService;

    @Override
    public DashboardResponse getDashboardData(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
        Optional<Goal> goalOpt = goalRepository.findByUserId(userId);

        boolean onboardingComplete = profileOpt.isPresent() && goalOpt.isPresent();

        Double bmr = null;
        Double tdee = null;
        String bmrFormula = null;

        if (profileOpt.isPresent()) {
            BMRCalculationResponse calculation = bmrCalculationService.calculateBMR(
                user.getGender(),
                user.getAge(),
                profileOpt.get().getHeightCm(),
                profileOpt.get().getWeightKg(),
                profileOpt.get().getBodyFatPercent(),
                profileOpt.get().getActivityLevel()
            );
            bmr = calculation.bmr();
            tdee = calculation.tdee();
            bmrFormula = calculation.formula();
        }

        return new DashboardResponse(
            user.getName(),
            user.getGender(),
            user.getAge(),
            profileOpt.map(UserProfile::getHeightCm).orElse(null),
            profileOpt.map(UserProfile::getWeightKg).orElse(null),
            profileOpt.map(UserProfile::getBodyFatPercent).orElse(null),
            profileOpt.map(UserProfile::getActivityLevel).orElse(null),
            goalOpt.map(Goal::getGoalType).orElse(null),
            goalOpt.map(Goal::getTargetWeightKg).orElse(null),
            goalOpt.map(Goal::getTargetTimelineWeeks).orElse(null),
            goalOpt.map(Goal::getPlanDetailLevel).orElse(null),
            bmr,
            tdee,
            bmrFormula,
            onboardingComplete,
            onboardingComplete && bmr != null && tdee != null
        );
    }
}