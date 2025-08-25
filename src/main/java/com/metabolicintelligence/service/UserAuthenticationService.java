package com.metabolicintelligence.service;

import com.metabolicintelligence.domain.User;

public interface UserAuthenticationService {
    User getCurrentUser();

    Integer getCurrentUserId();

    String getCurrentUserEmail();
}