package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateUserRequest;
import com.sleepystack.bankingapp.dto.LoginRequest;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.JsonWebTokenService;
import com.sleepystack.bankingapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
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
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        return userService.createUser(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                return jsonWebTokenService.generateToken(request.getEmail());
                }
        } catch (AuthenticationException e) {
            // Invalid credentials
        }
        return ("Invalid email or password");
    }
}
