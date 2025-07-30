package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.util.JsonWebTokenSigningKey;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonWebTokenService {
    private final JsonWebTokenSigningKey jsonWebTokenSigningKey;

    @Autowired
    public JsonWebTokenService(JsonWebTokenSigningKey jsonWebTokenSigningKey) {
        this.jsonWebTokenSigningKey = jsonWebTokenSigningKey;
    }
    public String generateToken(String subject) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return Jwts.builder()
                .claims()
                    .add(claims)
                    .subject(subject)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jsonWebTokenSigningKey.getExpirationTime()))
                    .and()
                .signWith(jsonWebTokenSigningKey.getSecretKey())
                .compact();
    }
}
