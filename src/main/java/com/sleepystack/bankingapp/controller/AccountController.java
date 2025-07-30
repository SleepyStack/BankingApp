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
        log.info("Request to create account for user: {}, type: {}, initial balance: {}", userPublicId, accountTypePublicIdentifier, request.getInitialBalance());
        Account account = new Account();
        account.setBalance(request.getInitialBalance());
        Account created = accountService.createAccountWithUserAndType(userPublicId, accountTypePublicIdentifier, account);
        log.info("Successfully created account {} for user: {}", created.getAccountNumber(), userPublicId);
        return created;
    }

    @GetMapping
    public List<Account> getAllAccountsForUser(@PathVariable String userPublicId) {
        log.info("Fetching all accounts for user: {}", userPublicId);
        List<Account> accounts = accountService.findAllByUserPublicId(userPublicId);
        log.info("Found {} accounts for user: {}", accounts.size(), userPublicId);
        return accounts;
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String userPublicId,
                              @PathVariable String accountNumber) {
        log.info("Fetching account {} for user: {}", accountNumber, userPublicId);
        Account account = accountService.getByUserPublicIdAndAccountNumber(userPublicId, accountNumber);
        log.info("Account {} fetched for user: {}", accountNumber, userPublicId);
        return account;
    }

    @DeleteMapping("/{accountNumber}")
    public void deleteAccount(@PathVariable String accountNumber){
        log.info("Request to delete account: {}", accountNumber);
        accountService.deleteAccountByAccountNumber(accountNumber);
        log.info("Deleted account: {}", accountNumber);
    }
}