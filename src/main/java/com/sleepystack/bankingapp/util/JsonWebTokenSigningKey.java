package com.sleepystack.bankingapp.util;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Getter
public class JsonWebTokenSigningKey {
    private final SecretKey secretKey;
    private final long expirationTime = 3600000; // 1 hour in milliseconds

    public JsonWebTokenSigningKey(@Value("${jwt.secret}") String jwtSecret) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}
