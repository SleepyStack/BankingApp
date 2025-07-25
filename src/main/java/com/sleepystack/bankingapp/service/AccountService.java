package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.DuplicateKeyException;
import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.repository.AccountTypeRepository;
import com.sleepystack.bankingapp.util.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountTypeRepository accountTypeRepository;

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
            try {
                return accountRepository.save(account);
            } catch (DuplicateKeyException e) {
                if (attempt == maxTries) {
                    throw new DuplicateKeyException("Failed to create account after " + maxTries + " attempts due to duplicate account numbers.");
                }
            }
        }
        throw new IllegalStateException("Unable to create account after retries"); // Should never reach here
    }

    public List<Account> findAllByUserPublicId(String userPublicId) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return accountRepository.findAllByUserId(user.getId());
    }

    public Account getByUserPublicIdAndAccountNumber(String userPublicId, String accountNumber) {
        User user = userRepository.findByPublicIdentifier(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for user"));
    }

    public void deleteAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        accountRepository.deleteById(account.getId());
    }
}