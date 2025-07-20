package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }
    public Transaction deposit(String accountId, double amount, String userId, String descrition){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if(amount <= 0){
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        Transaction transaction = new Transaction(null, accountId, "deposit", amount, Instant.now(), null, "completed", userId, "Money Deposited via SELF");
        return transactionRepository.save(transaction);
    }
    public Transaction withdraw(String accountId, double amount, String userId, String descrition){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if(amount <= 0){
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }
        if(account.getBalance() < amount){
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        Transaction transaction = new Transaction(null, accountId, "Withdrawal", amount, Instant.now(), null, "completed", userId, "Money Withdrawn via SELF");
        return transactionRepository.save(transaction);
    }
}
