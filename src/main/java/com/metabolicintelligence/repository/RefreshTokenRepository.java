package com.metabolicintelligence.repository;

import com.metabolicintelligence.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(Integer userId);
}
