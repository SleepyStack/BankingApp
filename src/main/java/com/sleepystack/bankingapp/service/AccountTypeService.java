package com.sleepystack.bankingapp.service;

import com.sleepystack.bankingapp.exception.ResourceNotFoundException;
import com.sleepystack.bankingapp.model.AccountType;
import com.sleepystack.bankingapp.repository.AccountTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountTypeService {
    private final AccountTypeRepository accountTypeRepository;

    @Autowired
    public AccountTypeService(AccountTypeRepository accountTypeRepository) {
        this.accountTypeRepository = accountTypeRepository;
    }

    public AccountType createAccountType(AccountType accountType) {
        AccountType saved = accountTypeRepository.save(accountType);
        log.info("Created account type [{}] with publicIdentifier [{}]", saved.getTypeName(), saved.getPublicIdentifier());
        return saved;
    }

    public List<AccountType> getAllAccountTypes() {
        List<AccountType> types = accountTypeRepository.findAll();
        log.info("Fetched all account types, count: {}", types.size());
        return types;
    }

    public AccountType getAccountTypeById(String id) {
        AccountType type = accountTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account type not found for id: {}", id);
                    return new ResourceNotFoundException("Account type not found");
                });
        log.info("Fetched account type [{}] for id: {}", type.getTypeName(), id);
        return type;
    }

    public AccountType updateAccountType(String id, AccountType updatedType) {
        AccountType existingType = accountTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account type not found for update, id: {}", id);
                    return new ResourceNotFoundException("Account type not found");
                });
        updatedType.setId(id);
        AccountType saved = accountTypeRepository.save(updatedType);
        log.info("Updated account type [{}] (id: {})", saved.getTypeName(), id);
        return saved;
    }

    public void deleteAccountType(String id) {
        AccountType existingType = accountTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account type not found for deletion, id: {}", id);
                    return new ResourceNotFoundException("Account type not found");
                });
        accountTypeRepository.deleteById(id);
        log.info("Deleted account type [{}] (id: {})", existingType.getTypeName(), id);
    }
}