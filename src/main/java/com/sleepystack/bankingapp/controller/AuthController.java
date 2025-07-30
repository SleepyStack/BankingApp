package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateUserRequest;
import com.sleepystack.bankingapp.dto.LoginRequest;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.JsonWebTokenService;
import com.sleepystack.bankingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
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
    public User register(@RequestBody @Valid CreateUserRequest request){
        log.info("Request to register user with email: {}", request.getEmail());
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        User created = userService.createUser(user);
        log.info("Registered user with email: {}", created.getEmail());
        return created;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                log.info("User {} logged in successfully", request.getEmail());
                return jsonWebTokenService.generateToken(request.getEmail());
            }
        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email '{}': {}", request.getEmail(), e.getMessage());
        }
        return ("Invalid email or password");
    }
}