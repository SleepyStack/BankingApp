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
//TODO add validation for userPublicId to ensure the user owns the account
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }
    public Transaction deposit(String userPublicId, String accountNumber, double amount, String description) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if(amount <= 0){
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description
                : String.format("Deposit of $%.2f to Account %s by User %s.", amount, accountNumber, account.getUserId());
        Transaction transaction = new Transaction(null,account.getId(), accountNumber,"deposit",amount, Instant.now(),
                null, null,"Completed", account.getUserId(), finalDescription);
        return transactionRepository.save(transaction);
    }
    public Transaction withdrawal(String userPublicId, String accountNumber, double amount, String description){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if(amount <= 0){
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }
        if(account.getBalance() < amount){
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description
                : String.format("Withdrawal of $%.2f from Account %s by User %s.", amount, accountNumber, account.getUserId());
        Transaction transaction = new Transaction(null,account.getId(), accountNumber,"withdraw",amount, Instant.now(),
                null, null,"Completed", account.getUserId(), finalDescription);
        return transactionRepository.save(transaction);
    }
    public Transaction transfer(String userPublicId,String accountNumber,String targetAccountNumber,double amount, String description) {
        Account fromAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        Account toAccount = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }
        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds for transfer");
        }
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description
                : String.format("Transfer of $%.2f from Account %s to Account %s by User %s.", amount, accountNumber, targetAccountNumber, fromAccount.getUserId());
        Transaction transaction = new Transaction(null,fromAccount.getId(), accountNumber,"transfer",amount, Instant.now(),
                toAccount.getId(), targetAccountNumber,"Completed", fromAccount.getUserId(), finalDescription);
        return transactionRepository.save(transaction);
        }
}
