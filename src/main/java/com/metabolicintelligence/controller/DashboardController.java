package com.metabolicintelligence.controller;

import com.metabolicintelligence.dto.request.plan.TransformationPlanRequest;
import com.metabolicintelligence.dto.response.dashboard.DashboardResponse;
import com.metabolicintelligence.dto.response.plan.TransformationPlanResponse;
import com.metabolicintelligence.service.DashboardService;
import com.metabolicintelligence.service.PlanGenerationService;
import com.metabolicintelligence.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@Validated
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    private final PlanGenerationService planGenerationService;
    private final UserAuthenticationService userAuthenticationService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        Integer userId = userAuthenticationService.getCurrentUserId();
        DashboardResponse response = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/generate-plan")
    public ResponseEntity<TransformationPlanResponse> generatePlan(
        @RequestBody @Valid TransformationPlanRequest request) {

        Integer userId = userAuthenticationService.getCurrentUserId();
        TransformationPlanResponse response = planGenerationService.generatePlan(userId, request);
        return ResponseEntity.ok(response);
    }
}