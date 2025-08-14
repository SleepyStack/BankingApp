package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateUserRequest;
import com.sleepystack.bankingapp.dto.UpdateUserRequest;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{publicId}")
    public User getUser(@PathVariable String publicId){
        log.info("Request to fetch user with public ID: {}", publicId);
        User user = userService.getUserByPublicId(publicId);
        log.info("Fetched user with public ID: {}", publicId);
        return user;
    }

    @GetMapping("/active")
    public List<User> getAllActiveUsers(){
        log.info("Request to fetch all active users");
        List<User> users = userService.getAllActiveUsers();
        log.info("Fetched {} active users", users.size());
        return users;
    }

    @GetMapping
    public List<User> getAllUsers(){
        log.info("Request to fetch all users");
        List<User> users = userService.getAllUsers();
        log.info("Fetched {} users", users.size());
        return users;
    }

    @PutMapping("/{publicId}")
    public User updateUser(@PathVariable String publicId, @RequestBody UpdateUserRequest request){
        log.info("Request to update user with public ID: {}", publicId);
        User user = userService.getUserByPublicId(publicId);
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        User updated = userService.updateUserByPublicId(publicId, user);
        log.info("Updated user with public ID: {}", publicId);
        return updated;
    }

    @DeleteMapping("/{publicId}")
    public void deleteUser(@PathVariable String publicId){
        log.info("Request to delete user with public ID: {}", publicId);
        userService.deleteUserByPublicId(publicId);
        log.info("Deleted user with public ID: {}", publicId);
    }
}