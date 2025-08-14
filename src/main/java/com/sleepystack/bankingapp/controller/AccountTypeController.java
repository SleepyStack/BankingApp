package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.service.AccountTypeService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/account-types")
@Slf4j
public class AccountTypeController {
    private final AccountTypeService accountTypeService;
    private static final Logger adminAuditLogger = LoggerFactory.getLogger("adminAuditLogger");

    @Autowired
    public AccountTypeController(AccountTypeService accountTypeService) {
        this.accountTypeService = accountTypeService;
    }

    @PostMapping()
    public AccountType createAccountType(@RequestBody AccountType type) {
        String publicIdentifier = type.getPublicIdentifier();
        if (publicIdentifier == null || !publicIdentifier.matches("^[A-Za-z]{2}$")) {
            log.warn("Attempted to create account type with invalid publicIdentifier: {}", publicIdentifier);
            throw new IllegalArgumentException("publicIdentifier must be exactly 2 alphabetic characters");
        }
        log.info("Request to create account type with public identifier: {}", publicIdentifier);
        AccountType created = accountTypeService.createAccountType(type);
        log.info("Created account type with ID: {}", created.getId());
        return created;
    }

    @GetMapping
    public List<AccountType> getAllAccountTypes() {
        log.info("Request to fetch all account types.");
        List<AccountType> types = accountTypeService.getAllAccountTypes();
        log.info("Found {} account types.", types.size());
        return types;
    }

    @GetMapping("/{id}")
    public AccountType getAccountTypeById(@PathVariable String id) {
        log.info("Request to fetch account type by ID: {}", id);
        AccountType type = accountTypeService.getAccountTypeById(id);
        log.info("Fetched account type: {} for ID: {}", type.getTypeName(), id);
        return type;
    }

    @PutMapping("/{id}")
    public AccountType updateAccountType(@PathVariable String id, @RequestBody AccountType updatedType) {
        log.info("Request to update account type with id: {}", id);
        AccountType type = accountTypeService.updateAccountType(id, updatedType);
        log.info("Updated account type with id: {}", id);
        return type;
    }

    @DeleteMapping("/{id}")
    public void deleteAccountType(@PathVariable String id) {
        log.info("Request to delete account type with id: {}", id);
        accountTypeService.deleteAccountType(id);
        log.info("Deleted account type with id: {}", id);
    }
}