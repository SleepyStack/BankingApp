package com.sleepystack.bankingapp.service;

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
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        AccountType accountType = accountTypeRepository.findByPublicIdentifier(accountTypePublicIdentifier)
                .orElseThrow(() -> new RuntimeException("Account type not found"));
        // Generate unique account number
        String accountNumber;
        do {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        account.setUserId(user.getId());
        account.setAccountTypeId(accountType.getId());
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }

    public List<Account> findAllByUserPublicId(String userPublicId) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findAllByUserId(user.getId());
    }

    public Account getByUserPublicIdAndAccountNumber(String userPublicId, String accountNumber) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new RuntimeException("Account not found for this user"));
    }

    public Account updateAccountForUser(String userPublicId, String accountNumber, Account updatedAccount) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account existingAccount = accountRepository.findByAccountNumberAndUserId(accountNumber, user.getId())
                .orElseThrow(() -> new RuntimeException("Account not found for this user"));
        existingAccount.setBalance(updatedAccount.getBalance());
        return accountRepository.save(existingAccount);
    }

    public void deleteAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.deleteById(account.getId());
    }
}