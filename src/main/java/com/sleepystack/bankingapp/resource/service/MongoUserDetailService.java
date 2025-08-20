package com.sleepystack.bankingapp.resource.service;

import com.sleepystack.bankingapp.common.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.common.repository.UserRepository;
import com.sleepystack.bankingapp.resource.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MongoUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public MongoUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return null;
    }
}
