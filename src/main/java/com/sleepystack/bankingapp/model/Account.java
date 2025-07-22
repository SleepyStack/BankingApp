package com.sleepystack.bankingapp.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
public class Account {
    @Id
    private String userId;

    private String id;
    private String accountNumber;
    private String accountTypeId;
    private double balance;

    public Account() {} // Default constructor required by Spring Data

    public Account(String userId, String id, String accountNumber, String accountTypeId, double balance) {
        this.userId = userId;
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountTypeId = accountTypeId;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(String accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
