package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userPublicId}/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    // Create new account for user & account type
    @PostMapping("/{accountTypePublicIdentifier}")
    public Account createAccount(@PathVariable String userPublicId,
                                 @PathVariable String accountTypePublicIdentifier,
                                 @RequestBody Account account) {
        return accountService.createAccountWithUserAndType(userPublicId, accountTypePublicIdentifier, account);
    }

    // List all accounts for user
    @GetMapping
    public List<Account> getAllAccountsForUser(@PathVariable String userPublicId) {
        return accountService.findAllByUserPublicId(userPublicId);
    }

    // Get specific account by account number for user
    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String userPublicId,
                              @PathVariable String accountNumber) {
        return accountService.getByUserPublicIdAndAccountNumber(userPublicId, accountNumber);
    }

    @DeleteMapping("/{accountNumber}")
    public void deleteAccount(@PathVariable String accountNumber){
        accountService.deleteAccountByAccountNumber(accountNumber);
    }
}