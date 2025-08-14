package com.sleepystack.bankingapp.repository;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.enums.AccountStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAllByUserId(String userId);
    Optional<Account> findByAccountNumberAndUserId(String accountNumber, String userId);
    List<Account> findAllByUserIdAndAccountTypeId(String userId, String accountTypeId);
    List<Account> findAllByUserIdAndStatus(String userId, AccountStatus status);
}
