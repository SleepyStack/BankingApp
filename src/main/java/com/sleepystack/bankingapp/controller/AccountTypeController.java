package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.service.AccountTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/account-types")
public class AccountTypeController {
    private final AccountTypeService accountTypeService;

    @Autowired
    public AccountTypeController(AccountTypeService accountTypeService) {
        this.accountTypeService = accountTypeService;
    }

    @PostMapping
    public AccountType createAccountType(@RequestBody AccountType type) {
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
        return accountTypeService.updateAccountType(id, updatedType);
    }

    @DeleteMapping("/{id}")
    public void deleteAccountType(@PathVariable String id) {
        accountTypeService.deleteAccountType(id);
    }
}