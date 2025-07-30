package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateAccountRequest;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users/{userPublicId}/accounts")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping("/{accountTypePublicIdentifier}")
    public Account createAccount(@PathVariable String userPublicId,
                                 @PathVariable String accountTypePublicIdentifier,
                                 @RequestBody @Valid CreateAccountRequest request) {
        Account account = new Account();
        account.setBalance(request.getInitialBalance());
        log.info("Creating account for user: {}, with type: {} having initial balance: {}", userPublicId, accountTypePublicIdentifier, request.getInitialBalance());
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
        Account account = accountService.getByUserPublicIdAndAccountNumber(userPublicId, accountNumber);
        log.info("Fetching account for user: {}, with account number: {}", userPublicId, accountNumber);
        return account;

    }

    @DeleteMapping("/{accountNumber}")
    public void deleteAccount(@PathVariable String accountNumber){
        accountService.deleteAccountByAccountNumber(accountNumber);
        log.info("Deleting account with account number: {}", accountNumber);
    }
}