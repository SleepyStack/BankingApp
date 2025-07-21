package com.sleepystack.bankingapp.service;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.repository.AccountRepository;
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
        return accountRepository.save(account);
    }
    public Optional<Account> findAccountById(String id) {
        return accountRepository.findById(id);
    }
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
    public Account updateAccount(String id, Account updatedAccount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        updatedAccount.setId(id);
        return accountRepository.save(updatedAccount);
    }
    public void deleteAccount(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.deleteById(id);
    }
}
