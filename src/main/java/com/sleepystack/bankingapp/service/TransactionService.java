package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.InsufficientFundsException;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.exception.UnauthorizedActionException;
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
    @Transactional
    public Transaction reverseTransaction(String adminPublicId, String transactionId, String reason) {
        User admin = userRepository.findByPublicIdentifier(adminPublicId)
                .orElseThrow(() -> {
                    log.warn("Admin user not found for reversing transaction, publicId: {}", adminPublicId);
                    return new ResourceNotFoundException("Admin user not found");
                });
        if (admin.getRoles() == null || !admin.getRoles().contains("ROLE_ADMIN")) {
            log.warn("User {} does not have admin rights for reversal", adminPublicId);
            throw new UnauthorizedActionException("Only admins can reverse transactions");
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found for reversal: {}", transactionId);
                    return new ResourceNotFoundException("Transaction not found");
                });

        if (!TransactionStatus.COMPLETED.equals(transaction.getStatus())) {
            log.warn("Transaction reversal denied: transaction {} is not completed", transactionId);
            throw new IllegalStateException("Transaction is not completed and cannot be reversed");
        }
        if (TransactionStatus.REVERSED.equals(transaction.getStatus())) {
            log.warn("Transaction {} is already reversed", transactionId);
            throw new IllegalStateException("Transaction is already reversed");
        }

        transaction.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(transaction);

        Account fromAccount = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Account not found for reversal: {}", transaction.getAccountId());
                    return new ResourceNotFoundException("Account not found");
                });

        Account toAccount = null;
        if (TransactionType.TRANSFER.equals(transaction.getType()) && transaction.getTargetAccountId() != null) {
            toAccount = accountRepository.findById(transaction.getTargetAccountId())
                    .orElseThrow(() -> {
                        log.warn("Target account not found for reversal: {}", transaction.getTargetAccountId());
                        return new ResourceNotFoundException("Target account not found");
                    });
        }

        Transaction reversalTxn = null;
        double amount = transaction.getAmount();

        switch (transaction.getType()) {
            case DEPOSIT:
                if (fromAccount.getBalance() < amount) {
                    log.warn("Deposit reversal denied: insufficient funds in account {}", fromAccount.getAccountNumber());
                    throw new InsufficientFundsException("Insufficient funds for deposit reversal");
                }
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                accountRepository.save(fromAccount);
                reversalTxn = new Transaction(
                        null,
                        fromAccount.getId(),
                        fromAccount.getAccountNumber(),
                        TransactionType.REVERSAL,
                        amount,
                        Instant.now(),
                        null,
                        null,
                        TransactionStatus.REVERSED,
                        admin.getId(),
                        String.format("Reversal of deposit txn %s by admin %s. Reason: %s", transactionId, adminPublicId, reason)
                );
                break;
            case WITHDRAWAL:
                fromAccount.setBalance(fromAccount.getBalance() + amount);
                accountRepository.save(fromAccount);
                reversalTxn = new Transaction(
                        null,
                        fromAccount.getId(),
                        fromAccount.getAccountNumber(),
                        TransactionType.REVERSAL,
                        amount,
                        Instant.now(),
                        null,
                        null,
                        TransactionStatus.REVERSED,
                        admin.getId(),
                        String.format("Reversal of withdrawal txn %s by admin %s. Reason: %s", transactionId, adminPublicId, reason)
                );
                break;
            case TRANSFER:
                if (toAccount == null) {
                    log.warn("Transfer reversal failed: target account not found");
                    throw new ResourceNotFoundException("Target account not found for transfer reversal");
                }
                if (toAccount.getBalance() < amount) {
                    log.warn("Transfer reversal denied: insufficient funds in target account {}", toAccount.getAccountNumber());
                    throw new InsufficientFundsException("Insufficient funds in target account for transfer reversal");
                }
                toAccount.setBalance(toAccount.getBalance() - amount);
                fromAccount.setBalance(fromAccount.getBalance() + amount);
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
                reversalTxn = new Transaction(
                        null,
                        fromAccount.getId(),
                        fromAccount.getAccountNumber(),
                        TransactionType.REVERSAL,
                        amount,
                        Instant.now(),
                        toAccount.getId(),
                        toAccount.getAccountNumber(),
                        TransactionStatus.REVERSED,
                        admin.getId(),
                        String.format("Reversal of transfer txn %s by admin %s. Reason: %s", transactionId, adminPublicId, reason)
                );
                break;
            default:
                log.warn("Unsupported transaction type: {}", transaction.getType());
                throw new IllegalArgumentException("Unsupported transaction type for reversal");
        }
        Transaction savedReversal = transactionRepository.save(reversalTxn);
        log.info("Reversed txn {} by admin {}. Reason: {}", transactionId, adminPublicId, reason);
        return savedReversal;
    }
}