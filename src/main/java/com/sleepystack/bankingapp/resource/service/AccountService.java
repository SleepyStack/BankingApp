package com.sleepystack.bankingapp.resource.service;

import com.sleepystack.bankingapp.common.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.resource.model.Account;
import com.sleepystack.bankingapp.resource.model.User;
import com.sleepystack.bankingapp.resource.model.AccountType;
import com.sleepystack.bankingapp.resource.enums.AccountStatus;
import com.sleepystack.bankingapp.common.repository.AccountRepository;
import com.sleepystack.bankingapp.common.repository.UserRepository;
import com.sleepystack.bankingapp.common.repository.AccountTypeRepository;
import com.sleepystack.bankingapp.common.util.UUIDIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

            String accountNumber = UUIDIdGenerator.generateUUID();
            account.setUserId(user.getId());
            account.setAccountTypeId(accountType.getId());
            account.setAccountNumber(accountNumber);
            account.setStatus(AccountStatus.ACTIVE);
        Account saved = accountRepository.save(account);
        log.info("Created new account [{}] for user [{}] with type [{}]", accountNumber, userPublicId, accountTypePublicIdentifier);
        return saved;
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

    @PreAuthorize("hasRole('ADMIN')")
    public void closeAccountByAccountNumber(String accountNumber) {
        User actingAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        log.info("Closed account [{}]", accountNumber);
        adminAuditLogger.info("Admin [{}] closed account [{}]", actingAdmin.getPublicIdentifier(), accountNumber);
    }
}