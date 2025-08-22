package com.metabolicintelligence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.metabolicintelligence.domain.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Integer> {
    Optional<RevokedToken> findByToken(String token);
}
