package com.sleepystack.bankingapp.repository;

import com.sleepystack.bankingapp.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
    List<Transaction> findByInitiatedByUserIdOrderByTimestampDesc(String userId);
}
