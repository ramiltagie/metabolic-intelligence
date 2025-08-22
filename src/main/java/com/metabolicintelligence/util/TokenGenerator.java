package com.metabolicintelligence.util;

import com.metabolicintelligence.config.jwt.JwtProperties;
import com.metabolicintelligence.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenGenerator {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenGenerator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusSeconds(jwtProperties.getAccessTokenValiditySeconds()));

        return Jwts.builder()
            .setIssuer(jwtProperties.getIssuer())
            .setAudience(jwtProperties.getAudience())
            .setSubject(user.getEmail())
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .claim("gender", user.getGender().name())
            .claim("age", user.getAge())
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
