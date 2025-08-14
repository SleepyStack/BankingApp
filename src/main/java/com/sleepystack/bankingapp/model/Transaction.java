package com.sleepystack.bankingapp.model;

import com.sleepystack.bankingapp.enums.TransactionStatus;
import com.sleepystack.bankingapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "transactions")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Transaction {
    @Id
    private String id;

    private String accountId;
    private String accountNumber;
    private TransactionType type;
    private double amount;
    private Instant timestamp;
    private String targetAccountId;
    private String targetAccountNumber;
    private TransactionStatus status;
    private String initiatedByUserId;
    private String description;
}