package com.sleepystack.bankingapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class UserResponse {
    private String publicIdentifier;
    private String name;
    private String email;
    private String phone;
    private List<String> roles;
    private String status;
    private String lastLoginTime;
    private int loginAttempts;
    private Instant createdAt;
    private Instant updatedAt;
}