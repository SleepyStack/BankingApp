package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountTypeService {
    private final AccountTypeRepository accountTypeRepository;
    @Autowired
    public AccountTypeService(AccountTypeRepository accountTypeRepository) {
        this.accountTypeRepository = accountTypeRepository;
    }

    public AccountType createAccountType(AccountType accountType) {
        return accountTypeRepository.save(accountType);
    }

    public List<AccountType> getAllAccountTypes() {
        return accountTypeRepository.findAll();
    }

    public AccountType getAccountTypeById(String id) {
        return accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found"));
    }

    public AccountType updateAccountType(String id, AccountType updatedType) {
        AccountType existingType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found"));
        updatedType.setId(id);
        return accountTypeRepository.save(updatedType);
    }

    public void deleteAccountType(String id) {
        AccountType existingType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found"));
        accountTypeRepository.deleteById(id);
    }
}