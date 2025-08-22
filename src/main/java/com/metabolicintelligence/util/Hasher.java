package com.metabolicintelligence.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Hasher {
    private final BCryptPasswordEncoder encoder;

    public Hasher() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
