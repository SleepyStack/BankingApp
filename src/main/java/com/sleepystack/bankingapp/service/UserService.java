package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.util.UserIdGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new ArrayList<>(List.of("ROLE_USER"))); // Default role
        }
        return userRepository.save(user);
    }

    public User getUserByPublicId(String publicId) {
        return userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserByPublicId(String publicId, User updatedUser) {
        User existingUser = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        updatedUser.setId(existingUser.getId());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
                updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }
        return userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUserByPublicId(String publicId) {
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        accountRepository.deleteAll(accounts);
        userRepository.deleteById(user.getId());
    }

    public User getUserByEmail(@Email(message = "Invalid email address") @NotBlank(message = "Email is required") String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}