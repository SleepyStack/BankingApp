package com.sleepystack.bankingapp.common.repository;

import com.sleepystack.bankingapp.resource.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface TransactionRepository extends MongoRepository<Transaction, String>, TransactionRepositoryCustom {
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
}