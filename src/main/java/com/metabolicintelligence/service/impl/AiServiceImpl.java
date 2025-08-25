package com.metabolicintelligence.service.impl;

import com.metabolicintelligence.domain.Goal;
import com.metabolicintelligence.domain.User;
import com.metabolicintelligence.domain.UserProfile;
import com.metabolicintelligence.dto.request.ai.AiRequest;
import com.metabolicintelligence.dto.response.ai.AiResponse;
import com.metabolicintelligence.dto.response.onboarding.BMRCalculationResponse;
import com.metabolicintelligence.external.openai.chat.ChatClient;
import com.metabolicintelligence.external.openai.chat.ChatMessage;
import com.metabolicintelligence.repository.GoalRepository;
import com.metabolicintelligence.repository.UserProfileRepository;
import com.metabolicintelligence.repository.UserRepository;
import com.metabolicintelligence.service.AiService;
import com.metabolicintelligence.service.BMRCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.metabolicintelligence.util.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";

    private static final String SYSTEM_PROMPT = "You are a professional fitness and nutrition coach " +
        "specializing in body transformation. You provide personalized advice based on " +
        "the user's profile, goals, and metabolic data. Always provide evidence-based, " +
        "safe advice. Be specific and actionable in your recommendations. " +
        "Consider the user's current stats, goals, and limitations. " +
        "If the user hasn't completed onboarding, encourage them to complete their profile first. " +
        "For nutrition advice, base recommendations on their BMR/TDEE if available. " +
        "For exercise advice, consider their activity level and goals. " +
        "Be supportive and motivational while being realistic about expectations. " +
        "If asked about medical conditions, always recommend consulting healthcare professionals. " +
        "Keep responses concise but comprehensive (aim for 200-400 words).";

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final GoalRepository goalRepository;
    private final BMRCalculationService bmrCalculationService;

    @Override
    public AiResponse getPersonalizedAdvice(Integer userId, AiRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
        Optional<Goal> goalOpt = goalRepository.findByUserId(userId);

        String userContext = buildUserContext(user, profileOpt, goalOpt);

        List<ChatMessage> messages = List.of(
            new ChatMessage(ROLE_SYSTEM, SYSTEM_PROMPT),
            new ChatMessage(ROLE_SYSTEM, "User Context: " + userContext),
            new ChatMessage(ROLE_USER, request.question())
        );

        try {
            String aiResponse = chatClient.chat(messages);
            return new AiResponse(aiResponse);
        } catch (Exception e) {
            return new AiResponse(
                "I'm sorry, I'm having trouble connecting to my knowledge base right now."
            );
        }
    }

    private String buildUserContext(User user, Optional<UserProfile> profileOpt, Optional<Goal> goalOpt) {
        StringBuilder context = new StringBuilder();

        context.append("User: ").append(user.getName())
            .append(", Age: ").append(user.getAge())
            .append(", Gender: ").append(user.getGender()).append("\\n");

        if (profileOpt.isPresent()) {
            UserProfile profile = profileOpt.get();
            context.append("Physical Stats - Height: ").append(profile.getHeightCm()).append("cm")
                .append(", Weight: ").append(profile.getWeightKg()).append("kg");

            if (profile.getBodyFatPercent() != null) {
                context.append(", Body Fat: ").append(profile.getBodyFatPercent()).append("%");
            }

            context.append("\\nActivity Level: ").append(profile.getActivityLevel());

            if (profile.getSleepHours() != null) {
                context.append("\\nSleep: ").append(profile.getSleepHours()).append(" hours/night");
            }

            if (profile.getDietNotes() != null && !profile.getDietNotes().trim().isEmpty()) {
                context.append("\\nDiet Notes: ").append(profile.getDietNotes());
            }

            try {
                BMRCalculationResponse bmrData = bmrCalculationService.calculateBMR(
                    user.getGender(), user.getAge(), profile.getHeightCm(),
                    profile.getWeightKg(), profile.getBodyFatPercent(), profile.getActivityLevel()
                );
                context.append("\\nBMR: ").append(bmrData.bmr()).append(" calories")
                    .append(", TDEE: ").append(bmrData.tdee()).append(" calories");
            } catch (Exception e) {
            }
        } else {
            context.append("\\nProfile: Not completed - encourage user to complete onboarding");
        }

        if (goalOpt.isPresent()) {
            Goal goal = goalOpt.get();
            context.append("\\nGoal: ").append(goal.getGoalType());

            if (goal.getTargetWeightKg() != null) {
                context.append(", Target Weight: ").append(goal.getTargetWeightKg()).append("kg");
            }

            if (goal.getTargetTimelineWeeks() != null) {
                context.append(", Timeline: ").append(goal.getTargetTimelineWeeks()).append(" weeks");
            }
        } else {
            context.append("\\nGoals: Not set - encourage user to complete onboarding");
        }

        return context.toString();
    }
}