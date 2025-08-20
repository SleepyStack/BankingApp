package com.sleepystack.bankingapp.resource.service;

import com.sleepystack.bankingapp.resource.enums.UserRole;
import com.sleepystack.bankingapp.common.exception.DuplicateKeyException;
import com.sleepystack.bankingapp.common.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.common.exception.UnauthorizedActionException;
import com.sleepystack.bankingapp.resource.model.Account;
import com.sleepystack.bankingapp.resource.enums.AccountStatus;
import com.sleepystack.bankingapp.resource.enums.UserStatus;
import com.sleepystack.bankingapp.common.repository.AccountRepository;
import com.sleepystack.bankingapp.common.repository.UserRepository;
import com.sleepystack.bankingapp.resource.model.User;
import com.sleepystack.bankingapp.common.util.UUIDIdGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger adminAuditLogger = LoggerFactory.getLogger("adminAuditLogger");

    @Autowired
    public UserService(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String publicId = UUIDIdGenerator.generateUUID();
        user.setPublicIdentifier(publicId);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateKeyException("Email already registered.");
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new DuplicateKeyException("Phone number already registered.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new ArrayList<>(List.of(UserRole.ROLE_USER.name())));
        }
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);
        log.info("Created user [{}] with email [{}]", saved.getPublicIdentifier(), saved.getEmail());
        return saved;
    }

    public User getUserByPublicId(String publicId) {
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> {
                    log.warn("User not found for publicId: {}", publicId);
                    return new ResourceNotFoundException("User not found");
                });
        log.info("Fetched user [{}] with email [{}]", publicId, user.getEmail());
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Fetched all users, count: {}", users.size());
        return users;
    }

    public User updateUserByPublicId(String publicId, User updatedUser) {
        User existingUser = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> {
                    log.warn("User not found for update, publicId: {}", publicId);
                    return new ResourceNotFoundException("User not found");
                });
        updatedUser.setId(existingUser.getId());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
                updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }
        User saved = userRepository.save(updatedUser);
        log.info("Updated user [{}] with email [{}]", publicId, saved.getEmail());
        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserByPublicId(String publicId) {
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion, publicId: {}", publicId);
                    return new ResourceNotFoundException("User not found");
                });
        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        if (!accounts.isEmpty()) {
            for (Account account : accounts) {
                account.setStatus(AccountStatus.CLOSED);
                accountRepository.save(account);
                log.info("Closed account [{}] for user [{}]", account.getAccountNumber(), publicId);
            }
        }
        user.setStatus(UserStatus.DEACTIVATED);
        log.info("Deleted user [{}] and all their accounts", publicId);
        adminAuditLogger.info("Admin [{}] deleted user [{}]", SecurityContextHolder.getContext().getAuthentication().getName(), publicId);
    }

    public User getUserByEmail(@Email(message = "Invalid email address") @NotBlank(message = "Email is required") String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        log.info("Fetched user [{}] with email [{}]", user.getPublicIdentifier(), email);
        return user;
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findAllByStatus(UserStatus.ACTIVE);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String publicId) {
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> {
                    log.warn("User not found for authorities, publicId: {}", publicId);
                    return new ResourceNotFoundException("User not found with publicId: " + publicId);
                });
        return user.getRoles().stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role) // Ensure "ROLE_" prefix
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public void resetPasswordWithOldPassword(String email, @NotBlank(message = "Old password is required") String oldPassword, @NotBlank(message = "New password is required") String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Old password does not match for user: {}", email);
            throw new UnauthorizedActionException("Old password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset successful for user: {}", email);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public User promoteToAdmin(String publicId) {
        User actingAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<String> roles = user.getRoles();
        if (!roles.contains("ROLE_ADMIN")) {
            roles.add("ROLE_ADMIN");
            user.setRoles(roles);
            userRepository.save(user);
            log.info("Promoted user [{}] to admin.", publicId);
        }
        adminAuditLogger.info("Admin [{}] promoted user [{}] to admin.", actingAdmin.getPublicIdentifier(), publicId);
        return user;
    }
}