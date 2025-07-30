package com.sleepystack.bankingapp.util;

import com.sleepystack.bankingapp.dto.UserResponse;
import com.sleepystack.bankingapp.model.User;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        UserResponse resp = new UserResponse();
        resp.setPublicIdentifier(user.getPublicIdentifier());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setRoles(user.getRoles());
        resp.setStatus(user.getStatus());
        resp.setLastLoginTime(user.getLastLoginTime());
        resp.setLoginAttempts(user.getLoginAttempts());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setUpdatedAt(user.getUpdatedAt());
        return resp;
    }
}