package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
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
    public User updateUser(@PathVariable String publicId, @RequestBody User user){
        return userService.updateUserByPublicId(publicId, user);
    }
    @DeleteMapping("/{publicId}")
    public void deleteUser(@PathVariable String publicId){
        userService.deleteUserByPublicId(publicId);
    }
}
