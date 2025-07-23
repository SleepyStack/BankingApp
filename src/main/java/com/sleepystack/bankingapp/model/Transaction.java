package com.sleepystack.bankingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;

    private String accountId;
    private String accountNumber;
    private String type;
    private double amount;
    private Instant timestamp;
    private String targetAccountId;
    private String targetAccountNumber;
    private String status;
    private String initiatedByUserId;
    private String description;

    public Transaction() {}

    public Transaction(String id, String accountId, String accountNumber, String type, double amount, Instant timestamp, String targetAccountId, String targetAccountNumber, String status, String initiatedByUserId, String description) {
        this.id = id;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.targetAccountId = targetAccountId;
        this.targetAccountNumber = targetAccountNumber;
        this.status = status;
        this.initiatedByUserId = initiatedByUserId;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public void setTargetAccountNumber(String targetAccountNumber) {
        this.targetAccountNumber = targetAccountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInitiatedByUserId() {
        return initiatedByUserId;
    }

    public void setInitiatedByUserId(String initiatedByUserId) {
        this.initiatedByUserId = initiatedByUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}