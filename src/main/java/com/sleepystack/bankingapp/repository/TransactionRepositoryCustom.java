package com.sleepystack.bankingapp.repository;

import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.enums.TransactionType;
import com.sleepystack.bankingapp.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface TransactionRepositoryCustom {
    Page<Transaction> findTransactionsWithFilters(
            String accountNumber,
            TransactionType type,
            TransactionStatus status,
            Double minAmount,
            Double maxAmount,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    );
}