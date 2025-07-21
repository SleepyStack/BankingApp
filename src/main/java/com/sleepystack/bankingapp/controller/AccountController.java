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
    public Account CreateAccount(@RequestBody Account account){
        return accountService.createAccount(account);
    }
    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable String id){
        return accountService.findAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
    @GetMapping
    public List<Account> getAllAccounts(){
        return accountService.findAll();
    }
    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable String id, @RequestBody Account account){
        return accountService.updateAccount(id, account);
    }
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable String id){
        accountService.deleteAccount(id);
    }
} 
