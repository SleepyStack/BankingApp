package com.sleepystack.bankingapp.repository;
import com.sleepystack.bankingapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByPublicIdentifier(String publicId);
    Optional<User> findByEmail(String email);
}
