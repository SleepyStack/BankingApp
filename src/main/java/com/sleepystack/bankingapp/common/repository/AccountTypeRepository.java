package com.sleepystack.bankingapp.common.repository;
import com.sleepystack.bankingapp.resource.model.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface AccountTypeRepository extends MongoRepository<AccountType, String> {
    Optional<AccountType> findByPublicIdentifier(String publicIdentifier);
}
