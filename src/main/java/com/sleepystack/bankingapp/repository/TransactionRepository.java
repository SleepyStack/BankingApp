package com.sleepystack.bankingapp.repository;

import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.model.enums.TransactionStatus;
import com.sleepystack.bankingapp.model.enums.TransactionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String>, TransactionRepositoryCustom {
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
}