package com.metabolicintelligence.service.impl;

import com.metabolicintelligence.config.jwt.JwtProperties;
import com.metabolicintelligence.domain.RefreshToken;
import com.metabolicintelligence.domain.RevokedToken;
import com.metabolicintelligence.domain.User;
import com.metabolicintelligence.dto.request.auth.LoginRequest;
import com.metabolicintelligence.dto.request.auth.RefreshTokenRequest;
import com.metabolicintelligence.dto.request.auth.SignupRequest;
import com.metabolicintelligence.dto.response.auth.AuthResponse;
import com.metabolicintelligence.dto.response.auth.UserInfoResponse;
import com.metabolicintelligence.repository.RefreshTokenRepository;
import com.metabolicintelligence.repository.RevokedTokenRepository;
import com.metabolicintelligence.repository.UserRepository;
import com.metabolicintelligence.service.AuthService;
import com.metabolicintelligence.util.Hasher;
import com.metabolicintelligence.util.TokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.metabolicintelligence.util.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final Hasher hasher;
    private final TokenGenerator tokenGenerator;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public AuthResponse signup(@Valid SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DataIntegrityViolationException(EMAIL_IN_USE);
        }

        String salt = UUID.randomUUID().toString();
        String passwordHash = hasher.hash(request.password() + salt);

        User user = User.builder()
            .name(request.name())
            .email(request.email())
            .passwordHash(passwordHash)
            .passwordSalt(salt)
            .gender(request.gender())
            .age(request.age())
            .createdAt(LocalDateTime.now())
            .build();

        User savedUser = userRepository.save(user);
        return createAuthResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(@Valid LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));

        String salt = user.getPasswordSalt();
        if (!hasher.matches(request.password() + salt, user.getPasswordHash())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        List<RefreshToken> existingTokens = refreshTokenRepository.findByUserId(user.getId());
        if (!existingTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(existingTokens);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return createAuthResponse(user);
    }

    @Override
    public UserInfoResponse getUserInfo(String accessToken) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .requireIssuer(jwtProperties.getIssuer())
            .requireAudience(jwtProperties.getAudience())
            .build()
            .parseClaimsJws(accessToken)
            .getBody();

        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        return new UserInfoResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getGender(),
            user.getAge(),
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(@Valid RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.token())
            .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new CredentialsExpiredException(REFRESH_TOKEN_EXPIRED);
        }

        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentEmail.equals(user.getEmail())) {
            throw new AccessDeniedException(REFRESH_TOKEN_MISMATCH);
        }

        String newRefreshTokenStr = tokenGenerator.generateRefreshToken();
        LocalDateTime newExpires = LocalDateTime.ofInstant(
            Instant.now().plusSeconds(jwtProperties.getRefreshTokenValiditySeconds()),
            ZoneOffset.UTC);

        refreshToken.setToken(newRefreshTokenStr);
        refreshToken.setExpiresAt(newExpires);
        refreshTokenRepository.save(refreshToken);

        String newAccessToken = tokenGenerator.generateAccessToken(user);
        return new AuthResponse(newAccessToken, newRefreshTokenStr);
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        List<RefreshToken> existingTokens = refreshTokenRepository.findByUserId(user.getId());
        if (!existingTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(existingTokens);
        }

        if (accessToken != null && !accessToken.isBlank()) {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

            Date expiryDate = claims.getExpiration();
            LocalDateTime expiresAt = LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneOffset.UTC);

            RevokedToken revoked = RevokedToken.builder()
                .token(accessToken)
                .expiresAt(expiresAt)
                .build();
            revokedTokenRepository.save(revoked);
        }
    }

    private AuthResponse createAuthResponse(User user) {
        String accessToken = tokenGenerator.generateAccessToken(user);
        String refreshTokenStr = tokenGenerator.generateRefreshToken();

        LocalDateTime refreshExpires = LocalDateTime.ofInstant(
            Instant.now().plusSeconds(jwtProperties.getRefreshTokenValiditySeconds()),
            ZoneOffset.UTC);

        RefreshToken refreshToken = RefreshToken.builder()
            .token(refreshTokenStr)
            .expiresAt(refreshExpires)
            .userId(user.getId())
            .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr);
    }
}
