package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.util.UserIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public void deleteUserByPublicId(String publicId){
        User user = userRepository.findByPublicIdentifier(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.deleteById(user.getId());
    }
}
