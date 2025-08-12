package com.sleepystack.bankingapp.repository;

import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.model.enums.TransactionType;
import com.sleepystack.bankingapp.model.enums.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    private MongoTemplate mongoTemplate;

    @Autowired
    public TransactionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public Page<Transaction> findTransactionsWithFilters(
            String accountNumber,
            TransactionType type,
            TransactionStatus status,
            Double minAmount,
            Double maxAmount,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    ) {
        Criteria criteria = new Criteria();

        if (accountNumber != null) {
            criteria.and("accountNumber").is(accountNumber);
        }
        if (type != null) {
            criteria.and("type").is(type);
        }
        if (status != null) {
            criteria.and("status").is(status);
        }
        if (minAmount != null || maxAmount != null) {
            Criteria amountCriteria = Criteria.where("amount");
            if (minAmount != null && maxAmount != null) {
                amountCriteria.gte(minAmount).lte(maxAmount);
            } else if (minAmount != null) {
                amountCriteria.gte(minAmount);
            } else {
                amountCriteria.lte(maxAmount);
            }
            criteria.andOperator(amountCriteria);
        }
        if (startDate != null || endDate != null) {
            Criteria dateCriteria = Criteria.where("timestamp");
            if (startDate != null && endDate != null) {
                dateCriteria.gte(startDate).lte(endDate);
            } else if (startDate != null) {
                dateCriteria.gte(startDate);
            } else {
                dateCriteria.lte(endDate);
            }
            criteria.andOperator(dateCriteria);
        }

        Query query = new Query(criteria).with(pageable);

        long total = mongoTemplate.count(query, Transaction.class);
        var results = mongoTemplate.find(query, Transaction.class);

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }
}