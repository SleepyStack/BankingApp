package com.sleepystack.bankingapp.common.repository;
import com.sleepystack.bankingapp.resource.model.User;
import com.sleepystack.bankingapp.resource.enums.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByPublicIdentifier(String publicId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findAllByStatus(UserStatus status);
}
