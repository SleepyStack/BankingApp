package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateUserRequest;
import com.sleepystack.bankingapp.dto.LoginRequest;
import com.sleepystack.bankingapp.dto.ResetPasswordRequest;
import com.sleepystack.bankingapp.dto.UserResponse;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.exception.UnauthorizedActionException;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.enums.UserStatus;
import com.sleepystack.bankingapp.service.JsonWebTokenService;
import com.sleepystack.bankingapp.service.UserService;
import com.sleepystack.bankingapp.util.UserMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final UserService userService;
    private final JsonWebTokenService jsonWebTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, JsonWebTokenService jsonWebTokenService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jsonWebTokenService = jsonWebTokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid CreateUserRequest request) {
        log.info("Request to register user with email: {}", request.getEmail());
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        User created = userService.createUser(user);
        log.info("Registered user with email: {}", created.getEmail());
        return UserMapper.toResponse(created);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = null;
        try {
            user = userService.getUserByEmail(request.getEmail());

            if (!UserStatus.ACTIVE.equals(user.getStatus())) {
                log.warn("Login attempt for non-active user: {} (status: {})", request.getEmail(), user.getStatus());
                return "Account is not active. Please contact support.";
            }

            if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setStatus(UserStatus.LOCKED);
                userService.updateUserByPublicId(user.getPublicIdentifier(), user);
                log.warn("User {} is locked due to too many failed login attempts", request.getEmail());
                return "Account is locked due to too many failed login attempts. Please contact support.";
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            if (authentication.isAuthenticated()) {
                log.info("User {} logged in successfully", request.getEmail());
                user.setLastLoginTime(Instant.now().toString());
                user.setLoginAttempts(0);
                userService.updateUserByPublicId(user.getPublicIdentifier(), user);
                return jsonWebTokenService.generateToken(request.getEmail());
            }

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email '{}': {}", request.getEmail(), e.getMessage());
            if (user != null) {
                int newAttempts = user.getLoginAttempts() + 1;
                user.setLoginAttempts(newAttempts);
                if (newAttempts >= MAX_LOGIN_ATTEMPTS) {
                    user.setStatus(UserStatus.LOCKED);
                    log.warn("User {} locked after too many failed attempts.", request.getEmail());
                }
                userService.updateUserByPublicId(user.getPublicIdentifier(), user);
            }
            throw new UnauthorizedActionException("Invalid email or password");
        } catch (ResourceNotFoundException ex) {
            // User not found
            throw new UnauthorizedActionException("Invalid email or password");
        }
        return "Invalid email or password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getEmail();
        log.info("Password reset attempt for user: {}", email);
        userService.resetPasswordWithOldPassword(email, request.getOldPassword(), request.getNewPassword());
        log.info("Password changed for user: {}", email);
        return "Password changed successfully.";
    }
}