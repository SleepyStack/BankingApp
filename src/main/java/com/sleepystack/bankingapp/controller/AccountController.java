package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account){
        return accountService.createAccount(account);
    }

    @GetMapping("/{accountNumber}")
    public Account getAccountByAccountNumber(@PathVariable String accountNumber){
        return accountService.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @GetMapping
    public List<Account> getAllAccounts(){
        return accountService.findAll();
    }

    @PutMapping("/{accountNumber}")
    public Account updateAccount(@PathVariable String accountNumber, @RequestBody Account account){
        return accountService.updateAccountByAccountNumber(accountNumber, account);
    }

    @DeleteMapping("/{accountNumber}")
    public void deleteAccount(@PathVariable String accountNumber){
        accountService.deleteAccountByAccountNumber(accountNumber);
    }
}