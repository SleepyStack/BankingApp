package com.sleepystack.bankingapp.repository;
import com.sleepystack.bankingapp.model.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTypeRepository extends MongoRepository<AccountType, String> {
    Optional<AccountType> findByPublicIdentifier(String publicIdentifier);
}
