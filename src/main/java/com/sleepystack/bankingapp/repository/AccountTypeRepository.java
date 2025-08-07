package com.sleepystack.bankingapp.repository;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.model.enums.AccountStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountTypeRepository extends MongoRepository<AccountType, String> {
    Optional<AccountType> findByPublicIdentifier(String publicIdentifier);
    List<Account> findAllByUserIdAndStatus(String userId, AccountStatus status);
}
