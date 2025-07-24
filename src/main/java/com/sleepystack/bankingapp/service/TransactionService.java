package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.TransactionRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    //TODO add validation for userPublicId to ensure the user owns the account
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
    public Transaction deposit(String userPublicId, String accountNumber, double amount, String description) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for this user"));
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
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for this user"));
        if(account.getBalance() < amount){
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description
                : String.format("Withdrawal of $%.2f from Account %s by User %s.", amount, accountNumber, account.getUserId());
        Transaction transaction = new Transaction(null,account.getId(), accountNumber,"withdrawal",amount, Instant.now(),
                null, null,"Completed", account.getUserId(), finalDescription);
        return transactionRepository.save(transaction);
    }
    public Transaction transfer(String userPublicId,String accountNumber,String targetAccountNumber,double amount, String description) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Account fromAccount = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for this user"));
        Account toAccount = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));
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

    public List<Transaction> getTransactionsForAccount(String userPublicId, String accountNumber) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for this user"));
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }
}
