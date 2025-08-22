package com.metabolicintelligence.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String EMAIL_IN_USE = "Email is already in use";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token expired";
    public static final String REFRESH_TOKEN_MISMATCH = "Refresh token does not belong to current user";
    public static final String USER_NOT_FOUND = "User not found";
}
