package com.metabolicintelligence.service;

import com.metabolicintelligence.dto.request.auth.LoginRequest;
import com.metabolicintelligence.dto.request.auth.RefreshTokenRequest;
import com.metabolicintelligence.dto.request.auth.SignupRequest;
import com.metabolicintelligence.dto.response.auth.AuthResponse;
import com.metabolicintelligence.dto.response.auth.UserInfoResponse;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponse signup(@Valid SignupRequest signupRequest);

    AuthResponse login(@Valid LoginRequest loginRequest);

    AuthResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest);

    UserInfoResponse getUserInfo(String accessToken);

    void logout(String accessToken);
}
