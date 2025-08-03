package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.DuplicateKeyException;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.util.UserIdGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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

    @Autowired
    public UserService(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String publicId = UserIdGenerator.generateUserId();
        user.setPublicIdentifier(publicId);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateKeyException("Email already registered.");
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new DuplicateKeyException("Phone number already registered.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new ArrayList<>(List.of("ROLE_USER"))); // Default role
        }
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
    public void deleteUserByPublicId(String publicId) {
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion, publicId: {}", publicId);
                    return new ResourceNotFoundException("User not found");
                });
        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        accountRepository.deleteAll(accounts);
        userRepository.deleteById(user.getId());
        log.info("Deleted user [{}] and all their accounts", publicId);
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
}