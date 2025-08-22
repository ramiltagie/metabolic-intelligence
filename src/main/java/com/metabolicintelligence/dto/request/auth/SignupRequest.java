package com.metabolicintelligence.dto.request.auth;

import com.metabolicintelligence.domain.Gender;
import jakarta.validation.constraints.*;

public record SignupRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotNull(message = "Gender is required")
    Gender gender,

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be positive")
    Integer age
) {
}
