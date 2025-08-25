package com.metabolicintelligence.service;

import com.metabolicintelligence.dto.request.plan.TransformationPlanRequest;
import com.metabolicintelligence.dto.response.plan.TransformationPlanResponse;

public interface PlanGenerationService {
    TransformationPlanResponse generatePlan(Integer userId, TransformationPlanRequest request);
}