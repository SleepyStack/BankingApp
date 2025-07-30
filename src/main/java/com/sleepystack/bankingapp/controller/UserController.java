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
        return userService.getUserByPublicId(publicId);
    }
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @PutMapping("/{publicId}")
    public User updateUser(@PathVariable String publicId, @RequestBody UpdateUserRequest request){
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
        return userService.updateUserByPublicId(publicId, user);
    }
    @DeleteMapping("/{publicId}")
    public void deleteUser(@PathVariable String publicId){
        log.info("Deleting user with public ID: {}", publicId);
        userService.deleteUserByPublicId(publicId);
    }
}
