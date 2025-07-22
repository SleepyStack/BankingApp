package com.sleepystack.bankingapp.service;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.repository.AccountRepository;
import com.sleepystack.bankingapp.util.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public Account createAccount(Account account) {
        String accountNumber;
        //TODO: accountNumber should be unique, so we need to externally check it via MONGO DB too.
        do {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }
    public Optional<Account> findAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found with account number: " + accountNumber));
        return accountRepository.findByAccountNumber(accountNumber);
    }
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
    public Account updateAccountByAccountNumber(String accountNumber, Account updatedAccount) {
        Account existing = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        updatedAccount.setId(existing.getId());
        return accountRepository.save(updatedAccount);
    }
    public void deleteAccountByAccountNumber(String accountNumber) {
        Account existing = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.deleteById(existing.getId());
    }
}
