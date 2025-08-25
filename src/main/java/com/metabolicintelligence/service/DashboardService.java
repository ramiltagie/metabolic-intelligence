package com.metabolicintelligence.service;

import com.metabolicintelligence.dto.response.dashboard.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardData(Integer userId);
}