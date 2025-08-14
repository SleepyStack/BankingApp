package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.DuplicateKeyException;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.model.enums.AccountStatus;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.repository.AccountTypeRepository;
import com.sleepystack.bankingapp.util.AccountNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountTypeRepository accountTypeRepository;
    private static final Logger adminAuditLogger = LoggerFactory.getLogger("adminAuditLogger");

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          AccountTypeRepository accountTypeRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    public Account createAccountWithUserAndType(String userPublicId, String accountTypePublicIdentifier, Account account) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AccountType accountType = accountTypeRepository.findByPublicIdentifier(accountTypePublicIdentifier)
                .orElseThrow(() -> new ResourceNotFoundException("Account type not found"));

        int maxTries = 5;
        for (int attempt = 1; attempt <= maxTries; attempt++) {
            String accountNumber;
            do {
                accountNumber = AccountNumberGenerator.generateAccountNumber();
            } while (accountRepository.existsByAccountNumber(accountNumber));
            account.setUserId(user.getId());
            account.setAccountTypeId(accountType.getId());
            account.setAccountNumber(accountNumber);
            account.setStatus(AccountStatus.ACTIVE);
            try {
                Account saved = accountRepository.save(account);
                log.info("Created new account [{}] for user [{}] with type [{}]", accountNumber, userPublicId, accountTypePublicIdentifier);
                return saved;
            } catch (DuplicateKeyException e) {
                log.warn("Duplicate account number [{}] generated for user [{}], attempt {}/{}", accountNumber, userPublicId, attempt, maxTries);
                if (attempt == maxTries) {
                    log.error("Failed to create account after {} attempts for user [{}]", maxTries, userPublicId);
                    throw new DuplicateKeyException("Failed to create account after " + maxTries + " attempts due to duplicate account numbers.");
                }
            } catch (Exception e) {
                log.error("Unexpected error while creating account for user [{}]: {}", userPublicId, e.getMessage(), e);
                throw e;
            }
        }
        log.error("Unable to create account after retries for user [{}]", userPublicId);
        throw new IllegalStateException("Unable to create account after retries");
    }

    public List<Account> findAllByUserPublicId(String userPublicId) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        log.info("Fetched {} accounts for user [{}]", accounts.size(), userPublicId);
        return accounts;
    }

    public Account getByUserPublicIdAndAccountNumber(String userPublicId, String accountNumber) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user"));
        log.info("Fetched account [{}] for user [{}]", accountNumber, userPublicId);
        return account;
    }

    public void closeAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(AccountStatus.CLOSED);
        log.info("Closed account [{}]", accountNumber);
    }
}