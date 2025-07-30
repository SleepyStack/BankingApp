package com.sleepystack.bankingapp.service;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonWebTokenService {
    public String generateToken(String subject) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour expiration ( To set a custom expiration time, you can modify the value in milliseconds)
                .and.signWith(getKey())
                .compact();

    }
}
