package com.metabolicintelligence.controller;

import com.metabolicintelligence.dto.request.ai.AiRequest;
import com.metabolicintelligence.dto.response.ai.AiResponse;
import com.metabolicintelligence.service.AiService;
import com.metabolicintelligence.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@Validated
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;
    private final UserAuthenticationService userAuthenticationService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(
        @RequestBody @Valid AiRequest request) {

        Integer userId = userAuthenticationService.getCurrentUserId();
        AiResponse response = aiService.getPersonalizedAdvice(userId, request);
        return ResponseEntity.ok(response);
    }
}