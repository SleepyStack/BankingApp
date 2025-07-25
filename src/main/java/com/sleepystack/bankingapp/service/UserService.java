package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.util.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public UserService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public User createUser(User user) {
        String publicId = UserIdGenerator.generateUserId();
        user.setPublicIdentifier(publicId);
        return userRepository.save(user);
    }
    public User getUserByPublicId(String publicId) {
        return userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User updateUserByPublicId(String publicId, User updatedUser){
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        updatedUser.setId(user.getId());
        return userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUserByPublicId(String publicId){

        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        accountRepository.deleteAll(accounts);
        userRepository.deleteById(user.getId());
    }
}
