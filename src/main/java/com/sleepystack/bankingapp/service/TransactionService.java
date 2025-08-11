package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.InsufficientFundsException;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.model.enums.TransactionStatus;
import com.sleepystack.bankingapp.model.enums.TransactionType;
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
        Transaction transaction = new Transaction(null,account.getId(), accountNumber, TransactionType.DEPOSIT,amount, Instant.now(),
                null, null,TransactionStatus.COMPLETED, account.getUserId(), finalDescription);
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
        Transaction transaction = new Transaction(null,account.getId(), accountNumber,TransactionType.WITHDRAWAL,amount, Instant.now(),
                null, null, TransactionStatus.COMPLETED, account.getUserId(), finalDescription);
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
        Transaction transaction = new Transaction(null,fromAccount.getId(), accountNumber,TransactionType.TRANSFER,amount, Instant.now(),
                toAccount.getId(), targetAccountNumber,TransactionStatus.COMPLETED, fromAccount.getUserId(), finalDescription);
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
    public Transaction reverseTransaction(String adminPublicId, String transactionId, String reason ){
        User admin = userRepository.findByPublicIdentifier(adminPublicId)
                .orElseThrow(() -> {
                    log.warn("Admin user not found for reversing transaction, publicId: {}", adminPublicId);
                    return new ResourceNotFoundException("Admin user not found");
                });
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found for reversal: {}", transactionId);
                    return new ResourceNotFoundException("Transaction not found");
                });
        if (!transaction.getStatus().equals(TransactionStatus.COMPLETED)) {
            log.warn("Transaction reversal denied: transaction {} is not completed", transactionId);
            throw new IllegalStateException("Transaction is not completed and cannot be reversed");
        }
        User primaryUser = userRepository.findById(transaction.getInitiatedByUserId())
                .orElseThrow(() -> {
                    log.warn("Primary user not found for transaction reversal: {}", transaction.getInitiatedByUserId());
                    return new ResourceNotFoundException("Primary user not found");
                });
        User targetUser = null;
        Account fromAccount = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Source account not found for transaction reversal: {}", transaction.getAccountId());
                    return new ResourceNotFoundException("Source account not found");
                });
        Account toAccount = null;
        if (transaction.getType().equals(TransactionType.TRANSFER)) {
            try {
                toAccount = accountRepository.findById(transaction.getTargetAccountId())
                        .orElseThrow(() -> {
                            log.warn("Target account not found for transaction reversal: {}", transaction.getTargetAccountId());
                            return new ResourceNotFoundException("Target account not found");
                        });
                if(toAccount != null){
                    Account finalToAccount = toAccount;
                    targetUser = userRepository.findById(toAccount.getUserId())
                            .orElseThrow(() -> {
                                log.warn("Target user not found for transaction reversal: {}", finalToAccount.getUserId());
                                return new ResourceNotFoundException("Target user not found");
                            });
                }
            } catch (ResourceNotFoundException e) {
                log.warn("Target account not found for transaction reversal: {}", transaction.getTargetAccountId());
                throw new ResourceNotFoundException("Target account not found");
            }
            Transaction reverseTransaction = new Transaction(null, fromAccount.getId(), fromAccount.getAccountNumber(),
                    TransactionType.REVERSAL, transaction.getAmount(), Instant.now(), toAccount != null ? toAccount.getId() : null,
                    toAccount != null ? toAccount.getAccountNumber() : null, TransactionStatus.REVERSED, admin.getId(),
                    String.format("Reversal of transaction %s by admin %s. Reason: %s", transactionId, adminPublicId, reason));
            log.info("Reversing transfer transaction: {} from account [{}] to [{}] by admin [{}]. Reason: {}",
                    transactionId, fromAccount.getAccountNumber(), toAccount != null ? toAccount.getAccountNumber() : "N/A", adminPublicId, reason);
            return transactionRepository.save(reverseTransaction);
        }
        if (transaction.getType().equals(TransactionType.WITHDRAWAL)) {
            if (fromAccount.getBalance() < transaction.getAmount()) {
                log.warn("Withdrawal reversal denied: insufficient funds in account {} for user {}", fromAccount.getAccountNumber(), adminPublicId);
                throw new InsufficientFundsException("Insufficient funds for withdrawal reversal");
            }
            fromAccount.setBalance(fromAccount.getBalance() + transaction.getAmount());
            accountRepository.save(fromAccount);
            Transaction reverseTransaction = new Transaction(null, fromAccount.getId(), fromAccount.getAccountNumber(),
                    TransactionType.REVERSAL, transaction.getAmount(), Instant.now(), null, null, TransactionStatus.REVERSED,
                    admin.getId(), String.format("Reversal of withdrawal transaction %s by admin %s. Reason: %s", transactionId, adminPublicId, reason));
            log.info("Reversing withdrawal transaction: {} from account [{}] by admin [{}]. Reason: {}",
                    transactionId, fromAccount.getAccountNumber(), adminPublicId, reason);
            return transactionRepository.save(reverseTransaction);
        }
        if (transaction.getType().equals(TransactionType.DEPOSIT)) {
            fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
            accountRepository.save(fromAccount);
            Transaction reverseTransaction = new Transaction(null, fromAccount.getId(), fromAccount.getAccountNumber(),
                    TransactionType.REVERSAL, transaction.getAmount(), Instant.now(), null, null, TransactionStatus.REVERSED,
                    admin.getId(), String.format("Reversal of deposit transaction %s by admin %s. Reason: %s", transactionId, adminPublicId, reason));
            log.info("Reversing deposit transaction: {} from account [{}] by admin [{}]. Reason: {}",
                    transactionId, fromAccount.getAccountNumber(), adminPublicId, reason);
            transactionRepository.save(reverseTransaction);
    }transaction.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(transaction);
        log.info("Transaction {} reversed by admin {}. Reason: {}", transactionId, adminPublicId, reason);
        return transaction;
    }
}