package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.repository.UserRepository;
import com.sleepystack.bankingapp.repository.AccountTypeRepository;
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

    public List<Account> findAllByUserPublicId(String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findAllByUserId(user.getId());
    }

    public Account getByUserPublicIdAndAccountNumber(String userPublicId, String accountNumber) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findByAccountNumber(accountNumber)
                .filter(acc -> acc.getUserId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Account not found for this user"));
    }

    public Account createAccountWithUserAndType(String userPublicId, String accountTypePublicId, Account account) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        AccountType accountType = accountTypeRepository.findByPublicIdentifier(accountTypePublicId)
                .orElseThrow(() -> new RuntimeException("Account type not found"));
        account.setUserId(user.getId());
        account.setAccountTypeId(accountType.getId());
        return accountRepository.save(account);
    }
}