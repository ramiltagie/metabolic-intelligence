package com.metabolicintelligence.service;

import com.metabolicintelligence.dto.request.ai.AiRequest;
import com.metabolicintelligence.dto.response.ai.AiResponse;

public interface AiService {
    AiResponse getPersonalizedAdvice(Integer userId, AiRequest request);
}