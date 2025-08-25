package com.metabolicintelligence.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metabolicintelligence.domain.Goal;
import com.metabolicintelligence.domain.User;
import com.metabolicintelligence.domain.UserProfile;
import com.metabolicintelligence.dto.request.plan.TransformationPlanRequest;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.dto.response.plan.TransformationPlanResponse;
import com.metabolicintelligence.external.openai.chat.ChatClient;
import com.metabolicintelligence.external.openai.chat.ChatMessage;
import com.metabolicintelligence.repository.GoalRepository;
import com.metabolicintelligence.repository.UserProfileRepository;
import com.metabolicintelligence.repository.UserRepository;
import com.metabolicintelligence.service.BMRCalculationService;
import com.metabolicintelligence.service.PlanGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.metabolicintelligence.util.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PlanGenerationServiceImpl implements PlanGenerationService {
    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";

    private static final String PLAN_GENERATION_PROMPT = """
        You are an expert fitness and nutrition coach specializing in body transformation. Generate a comprehensive weekly transformation plan based on the user's profile and goals.
        
        Provide the response in JSON format with these exact keys:
        {
          "weeklyPlan": "Overview of the weekly structure and approach",
          "calorieGoals": "Daily calorie targets and explanation",
          "macroBreakdown": "Protein, carbs, and fat distribution with rationale",
          "workoutRecommendations": "Exercise routine and training split",
          "mealSuggestions": "Sample meals and timing recommendations",
          "timelineTargets": "Weekly milestones and expected progress"
        }
        
        Guidelines:
        - Be specific with numbers (calories, macros, weights, reps)
        - Tailor recommendations to the user's goal type and activity level
        - Consider their current stats and timeline
        - Include practical, actionable advice
        - Account for their lifestyle factors (sleep, diet notes, etc.)
        - Provide realistic expectations based on their BMR/TDEE
        """;

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final GoalRepository goalRepository;
    private final BMRCalculationService bmrCalculationService;
    private final ObjectMapper objectMapper;

    @Override
    public TransformationPlanResponse generatePlan(Integer userId, TransformationPlanRequest request) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException("User profile not found. Complete onboarding first."));

        Goal goal = goalRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException("User goals not found. Complete onboarding first."));

        BMRCalculationResponse bmrData = bmrCalculationService.calculateBMR(
            user.getGender(), user.getAge(), profile.getHeightCm(),
            profile.getWeightKg(), profile.getBodyFatPercent(), profile.getActivityLevel()
        );

        String userContext = buildUserContext(user, profile, goal, bmrData, request);

        List<ChatMessage> messages = List.of(
            new ChatMessage(ROLE_SYSTEM, PLAN_GENERATION_PROMPT),
            new ChatMessage(ROLE_USER, userContext)
        );

        String aiResponse = chatClient.chat(messages);

        try {

            JsonNode planJson = objectMapper.readTree(aiResponse);

            return new TransformationPlanResponse(
                planJson.path("weeklyPlan").asText(),
                planJson.path("calorieGoals").asText(),
                planJson.path("macroBreakdown").asText(),
                planJson.path("workoutRecommendations").asText(),
                planJson.path("mealSuggestions").asText(),
                planJson.path("timelineTargets").asText(),
                LocalDateTime.now()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate transformation plan", e);
        }
    }

    private String buildUserContext(User user, UserProfile profile, Goal goal,
                                    BMRCalculationResponse bmrData, TransformationPlanRequest request) {
        StringBuilder context = new StringBuilder();

        context.append("USER PROFILE:\n");
        context.append("- Name: ").append(user.getName()).append("\n");
        context.append("- Age: ").append(user.getAge()).append(" years\n");
        context.append("- Gender: ").append(user.getGender()).append("\n");
        context.append("- Height: ").append(profile.getHeightCm()).append(" cm\n");
        context.append("- Weight: ").append(profile.getWeightKg()).append(" kg\n");

        if (profile.getBodyFatPercent() != null) {
            context.append("- Body Fat: ").append(profile.getBodyFatPercent()).append("%\n");
        }

        context.append("- Activity Level: ").append(profile.getActivityLevel()).append("\n");

        if (profile.getSleepHours() != null) {
            context.append("- Sleep: ").append(profile.getSleepHours()).append(" hours/night\n");
        }

        if (profile.getSmoking() != null) {
            context.append("- Smoking: ").append(profile.getSmoking() ? "Yes" : "No").append("\n");
        }

        if (profile.getAlcohol() != null) {
            context.append("- Alcohol: ").append(profile.getAlcohol() ? "Yes" : "No").append("\n");
        }

        if (profile.getDietNotes() != null && !profile.getDietNotes().trim().isEmpty()) {
            context.append("- Diet Notes: ").append(profile.getDietNotes()).append("\n");
        }

        context.append("\nMETABOLIC DATA:\n");
        context.append("- BMR: ").append(bmrData.bmr()).append(" calories\n");
        context.append("- TDEE: ").append(bmrData.tdee()).append(" calories\n");
        context.append("- Formula Used: ").append(bmrData.formula()).append("\n");

        context.append("\nGOALS:\n");
        context.append("- Primary Goal: ").append(goal.getGoalType()).append("\n");

        if (goal.getTargetWeightKg() != null) {
            context.append("- Target Weight: ").append(goal.getTargetWeightKg()).append(" kg\n");
        }

        if (goal.getTargetTimelineWeeks() != null) {
            context.append("- Timeline: ").append(goal.getTargetTimelineWeeks()).append(" weeks\n");
        }

        context.append("- Plan Detail Level: ").append(goal.getPlanDetailLevel()).append("\n");

        if (request.additionalNotes() != null && !request.additionalNotes().trim().isEmpty()) {
            context.append("\nADDITIONAL NOTES:\n");
            context.append(request.additionalNotes()).append("\n");
        }

        return context.toString();
    }
}