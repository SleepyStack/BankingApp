package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateUserRequest;
import com.sleepystack.bankingapp.dto.LoginRequest;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/register")
    public User createUser(@RequestBody @Valid CreateUserRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        return userService.createUser(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            return "Login successful for user: " + user.getName();
        } else {
            return "Invalid email or password";
        }
    }
}
