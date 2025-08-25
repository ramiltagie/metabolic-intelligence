package com.metabolicintelligence.repository;

import com.metabolicintelligence.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
    Optional<Goal> findByUserId(Integer userId);
}
