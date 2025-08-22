package com.metabolicintelligence.dto.response.auth;

import com.metabolicintelligence.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserInfoResponse(
    @NotNull Integer id,
    @NotBlank String name,
    @Email String email,
    @NotNull Gender gender,
    @NotNull Integer age,
    @NotNull LocalDateTime createdAt,
    LocalDateTime lastLoginAt
) {
}
