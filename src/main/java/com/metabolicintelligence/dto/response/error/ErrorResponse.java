package com.metabolicintelligence.dto.response.error;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String errorCode,
    String message,
    Map<String, String> details
) {
}
