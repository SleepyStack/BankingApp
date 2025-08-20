package com.sleepystack.bankingapp.common.repository;

import com.sleepystack.bankingapp.resource.model.Transaction;
import com.sleepystack.bankingapp.resource.enums.TransactionType;
import com.sleepystack.bankingapp.resource.enums.TransactionStatus;
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