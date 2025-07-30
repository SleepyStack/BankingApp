package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.service.AccountTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/account-types")
@Slf4j
public class AccountTypeController {
    private final AccountTypeService accountTypeService;

    @Autowired
    public AccountTypeController(AccountTypeService accountTypeService) {
        this.accountTypeService = accountTypeService;
    }

    @PostMapping
    public AccountType createAccountType(@RequestBody AccountType type) {
        // Manual validation for 2-letter alphabetic code
        String publicIdentifier = type.getPublicIdentifier();
        if (publicIdentifier == null || !publicIdentifier.matches("^[A-Za-z]{2}$")) {
            throw new IllegalArgumentException("publicIdentifier must be exactly 2 alphabetic characters");
        }
        log.info("Creating account type with public identifier: {}", publicIdentifier);
        return accountTypeService.createAccountType(type);
    }

    @GetMapping
    public List<AccountType> getAllAccountTypes() {
        return accountTypeService.getAllAccountTypes();
    }

    @GetMapping("/{id}")
    public AccountType getAccountTypeById(@PathVariable String id) {
        return accountTypeService.getAccountTypeById(id);
    }

    @PutMapping("/{id}")
    public AccountType updateAccountType(@PathVariable String id, @RequestBody AccountType updatedType) {
        log.info("Updating account type with id: {}", id);
        return accountTypeService.updateAccountType(id, updatedType);
    }

    @DeleteMapping("/{id}")
    public void deleteAccountType(@PathVariable String id) {
        log.info("Deleting account type with id: {}", id);
        accountTypeService.deleteAccountType(id);
    }
}