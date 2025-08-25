package com.metabolicintelligence.service.impl;

import com.metabolicintelligence.domain.Goal;
import com.metabolicintelligence.domain.User;
import com.metabolicintelligence.domain.UserProfile;
import com.metabolicintelligence.dto.request.onboarding.GoalRequest;
import com.metabolicintelligence.dto.request.onboarding.ProfileRequest;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.dto.response.onboarding.OnboardingResponse;
import com.metabolicintelligence.repository.GoalRepository;
import com.metabolicintelligence.repository.UserProfileRepository;
import com.metabolicintelligence.repository.UserRepository;
import com.metabolicintelligence.service.BMRCalculationService;
import com.metabolicintelligence.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.metabolicintelligence.util.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final GoalRepository goalRepository;
    private final BMRCalculationService bmrCalculationService;

    @Override
    @Transactional
    public OnboardingResponse createProfile(Integer userId, @Valid ProfileRequest request) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);

        UserProfile profile;
        if (existingProfile.isPresent()) {

            profile = existingProfile.get();
            updateProfileFromRequest(profile, request);
        } else {
            profile = UserProfile.builder()
                .userId(userId)
                .heightCm(request.heightCm())
                .weightKg(request.weightKg())
                .bodyFatPercent(request.bodyFatPercent())
                .activityLevel(request.activityLevel())
                .sleepHours(request.sleepHours())
                .dietNotes(request.dietNotes())
                .smoking(request.smoking())
                .alcohol(request.alcohol())
                .build();
        }

        userProfileRepository.save(profile);

        return getOnboardingStatus(userId);
    }

    @Override
    @Transactional
    public OnboardingResponse createGoal(Integer userId, @Valid GoalRequest request) {
        Optional<Goal> existingGoal = goalRepository.findByUserId(userId);

        Goal goal;
        if (existingGoal.isPresent()) {

            goal = existingGoal.get();
            updateGoalFromRequest(goal, request);
        } else {

            goal = Goal.builder()
                .userId(userId)
                .goalType(request.goalType())
                .targetWeightKg(request.targetWeightKg())
                .targetTimelineWeeks(request.targetTimelineWeeks())
                .planDetailLevel(request.planDetailLevel())
                .build();
        }

        goalRepository.save(goal);

        return getOnboardingStatus(userId);
    }

    @Override
    public OnboardingResponse getOnboardingStatus(Integer userId) {
        boolean profileCompleted = userProfileRepository.findByUserId(userId).isPresent();
        boolean goalCompleted = goalRepository.findByUserId(userId).isPresent();

        Double bmr = null;
        Double tdee = null;

        if (profileCompleted) {
            BMRCalculationResponse calculation = calculateUserMetabolics(userId);
            bmr = calculation.bmr();
            tdee = calculation.tdee();
        }

        String message = determineOnboardingMessage(profileCompleted, goalCompleted);

        return new OnboardingResponse(message, profileCompleted, goalCompleted, bmr, tdee);
    }

    @Override
    public BMRCalculationResponse calculateUserMetabolics(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException("User profile not found. Complete profile setup first."));

        return bmrCalculationService.calculateBMR(
            user.getGender(),
            user.getAge(),
            profile.getHeightCm(),
            profile.getWeightKg(),
            profile.getBodyFatPercent(),
            profile.getActivityLevel()
        );
    }

    @Override
    public boolean isOnboardingComplete(Integer userId) {
        boolean profileExists = userProfileRepository.findByUserId(userId).isPresent();
        boolean goalExists = goalRepository.findByUserId(userId).isPresent();
        return profileExists && goalExists;
    }

    private void updateProfileFromRequest(UserProfile profile, ProfileRequest request) {
        profile.setHeightCm(request.heightCm());
        profile.setWeightKg(request.weightKg());
        profile.setBodyFatPercent(request.bodyFatPercent());
        profile.setActivityLevel(request.activityLevel());
        profile.setSleepHours(request.sleepHours());
        profile.setDietNotes(request.dietNotes());
        profile.setSmoking(request.smoking());
        profile.setAlcohol(request.alcohol());
    }

    private void updateGoalFromRequest(Goal goal, GoalRequest request) {
        goal.setGoalType(request.goalType());
        goal.setTargetWeightKg(request.targetWeightKg());
        goal.setTargetTimelineWeeks(request.targetTimelineWeeks());
        goal.setPlanDetailLevel(request.planDetailLevel());
    }

    private String determineOnboardingMessage(boolean profileCompleted, boolean goalCompleted) {
        if (profileCompleted && goalCompleted) {
            return "Onboarding completed! You can now generate your personalized transformation plan.";
        } else if (profileCompleted) {
            return "Profile setup completed. Please set your transformation goals to continue.";
        } else if (goalCompleted) {
            return "Goals set successfully. Please complete your profile setup.";
        } else {
            return "Welcome! Please complete your profile and set your transformation goals.";
        }
    }
}