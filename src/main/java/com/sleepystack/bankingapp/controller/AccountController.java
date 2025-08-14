package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.CreateAccountRequest;
import com.sleepystack.bankingapp.model.Account;
import com.sleepystack.bankingapp.model.User;
import com.sleepystack.bankingapp.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users/{userPublicId}/accounts")
@Slf4j
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {
    private final AccountService accountService;
    private static final Logger adminAuditLogger = LoggerFactory.getLogger("adminAuditLogger");

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping("/{accountTypePublicIdentifier}")
    @PreAuthorize("#userPublicId == principal.publicIdentifier")
    @Operation(summary = "Create account", description = "Create a new account for a user of a specific type.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created"),
            @ApiResponse(responseCode = "404", description = "User or account type not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate account number")
    })
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
    @PreAuthorize("#userPublicId == principal.publicIdentifier or hasRole('ADMIN')")
    @Operation(summary = "List accounts", description = "Get all accounts for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of accounts"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<Account> getAllAccountsForUser(@PathVariable String userPublicId) {
        log.info("Fetching all accounts for user: {}", userPublicId);
        List<Account> accounts = accountService.findAllByUserPublicId(userPublicId);
        log.info("Found {} accounts for user: {}", accounts.size(), userPublicId);
        return accounts;
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("#userPublicId == principal.publicIdentifier or hasRole('ADMIN')")
    @Operation(summary = "Get account by number", description = "Fetch account details by account number for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account or user not found")
    })
    public Account getAccount(@PathVariable String userPublicId,
                              @PathVariable String accountNumber) {
        log.info("Fetching account {} for user: {}", accountNumber, userPublicId);
        Account account = accountService.getByUserPublicIdAndAccountNumber(userPublicId, accountNumber);
        log.info("Account {} fetched for user: {}", accountNumber, userPublicId);
        return account;
    }

    @DeleteMapping("/admin/{accountNumber}")
    @Operation(summary = "Close account", description = "Admin can close (soft-delete) an account by its number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account closed"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public void closeAccount(@PathVariable String accountNumber){
        User actingAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Request to delete account: {}", accountNumber);
        accountService.closeAccountByAccountNumber(accountNumber);
        log.info("Deleted account: {}", accountNumber);
        adminAuditLogger.info("Admin [{}] closed account [{}]", actingAdmin.getPublicIdentifier(), accountNumber);
    }
}