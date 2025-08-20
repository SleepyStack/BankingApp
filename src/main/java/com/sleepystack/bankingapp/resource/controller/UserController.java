package com.sleepystack.bankingapp.resource.controller;

import com.sleepystack.bankingapp.common.dto.UpdateUserRequest;
import com.sleepystack.bankingapp.resource.model.User;
import com.sleepystack.bankingapp.resource.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Tag(name = "Admin - Users", description = "Admin user management endpoints")

public class UserController {
    private final UserService userService;
    private static final Logger adminAuditLogger = LoggerFactory.getLogger("adminAuditLogger");

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Get user by public ID", description = "Fetch a user by their public identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User getUser(@PathVariable String publicId){
        log.info("Request to fetch user with public ID: {}", publicId);
        User user = userService.getUserByPublicId(publicId);
        log.info("Fetched user with public ID: {}", publicId);
        return user;
    }

    @GetMapping("/active")
    @Operation(summary = "List all active users", description = "Returns all users with active status.")
    @ApiResponse(responseCode = "200", description = "List of active users")
    public List<User> getAllActiveUsers(){
        log.info("Request to fetch all active users");
        List<User> users = userService.getAllActiveUsers();
        log.info("Fetched {} active users", users.size());
        return users;
    }

    @GetMapping
    @Operation(summary = "List all users", description = "Returns all users (any status).")
    @ApiResponse(responseCode = "200", description = "List of all users")
    public List<User> getAllUsers(){
        log.info("Request to fetch all users");
        List<User> users = userService.getAllUsers();
        log.info("Fetched {} users", users.size());
        return users;
    }

    @PutMapping("/{publicId}")
    @Operation(summary = "Update user", description = "Update user data by public identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Delete user", description = "Delete (deactivate) a user by public identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(@PathVariable String publicId){
        log.info("Request to delete user with public ID: {}", publicId);
        userService.deleteUserByPublicId(publicId);
        log.info("Deleted user with public ID: {}", publicId);
    }

    @PostMapping("/{publicId}/promote")
    @Operation(summary = "Promote to admin", description = "Promotes a user to admin status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User promoted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User promoteToAdmin(@PathVariable String publicId) {
        log.info("Request to promote user [{}] to admin.", publicId);
        User promoted = userService.promoteToAdmin(publicId);
        log.info("User [{}] promoted to admin.", publicId);
        return promoted;
    }
}