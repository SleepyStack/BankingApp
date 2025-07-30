package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.InsufficientFundsException;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.TransactionRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
    public Transaction deposit(String userPublicId, String accountNumber, double amount, String description) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> {
                    log.warn("User not found for deposit, publicId: {}", userPublicId);
                    return new ResourceNotFoundException("User not found");
                });
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> {
                    log.warn("Account not found for deposit: {} for user: {}", accountNumber, userPublicId);
                    return new ResourceNotFoundException("Account not found for this user");
                });
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description
                : String.format("Deposit of $%.2f to Account %s by User %s.", amount, accountNumber, account.getUserId());
        Transaction transaction = new Transaction(null,account.getId(), accountNumber,"deposit",amount, Instant.now(),
                null, null,"Completed", account.getUserId(), finalDescription);
        Transaction savedTxn = transactionRepository.save(transaction);
        log.info("Deposit: {} to account [{}] by user [{}]. Transaction ID: {}", amount, accountNumber, userPublicId, savedTxn.getId());
        return savedTxn;
    }
    public Transaction withdrawal(String userPublicId, String accountNumber, double amount, String description){
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> {
                    log.warn("User not found for withdrawal, publicId: {}", userPublicId);
                    return new ResourceNotFoundException("User not found");
                });
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> {
                    log.warn("Account not found for withdrawal: {} for user: {}", accountNumber, userPublicId);
                    return new ResourceNotFoundException("Account not found for this user");
                });
        if(account.getBalance() < amount){
            log.warn("Withdrawal denied: insufficient funds for account {} (requested: {}, available: {})", accountNumber, amount, account.getBalance());
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description
                : String.format("Withdrawal of $%.2f from Account %s by User %s.", amount, accountNumber, account.getUserId());
        Transaction transaction = new Transaction(null,account.getId(), accountNumber,"withdrawal",amount, Instant.now(),
                null, null,"Completed", account.getUserId(), finalDescription);
        Transaction savedTxn = transactionRepository.save(transaction);
        log.info("Withdrawal: {} from account [{}] by user [{}]. Transaction ID: {}", amount, accountNumber, userPublicId, savedTxn.getId());
        return savedTxn;
    }

    @Transactional
    public Transaction transfer(String userPublicId,String accountNumber,String targetAccountNumber,double amount, String description) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> {
                    log.warn("User not found for transfer, publicId: {}", userPublicId);
                    return new ResourceNotFoundException("User not found");
                });
        Account fromAccount = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> {
                    log.warn("Source account not found for transfer: {} for user: {}", accountNumber, userPublicId);
                    return new ResourceNotFoundException("Account not found for this user");
                });
        Account toAccount = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> {
                    log.warn("Target account not found for transfer: {}", targetAccountNumber);
                    return new ResourceNotFoundException("Target account not found");
                });
        if (fromAccount.getBalance() < amount) {
            log.warn("Transfer denied: insufficient funds in account {} for user {}", accountNumber, userPublicId);
            throw new InsufficientFundsException("Insufficient funds for transfer");
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
        Transaction savedTxn = transactionRepository.save(transaction);
        log.info("Transfer: {} from account [{}] to [{}] by user [{}]. Transaction ID: {}", amount, accountNumber, targetAccountNumber, userPublicId, savedTxn.getId());
        return savedTxn;
    }

    public List<Transaction> getTransactionsForAccount(String userPublicId, String accountNumber) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> {
                    log.warn("User not found for fetching transactions, publicId: {}", userPublicId);
                    return new ResourceNotFoundException("User not found");
                });
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> {
                    log.warn("Account not found for fetching transactions: {} for user: {}", accountNumber, userPublicId);
                    return new ResourceNotFoundException("Account not found for this user");
                });
        List<Transaction> txns = transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
        log.info("Fetched {} transactions for account [{}] by user [{}]", txns.size(), accountNumber, userPublicId);
        return txns;
    }
}