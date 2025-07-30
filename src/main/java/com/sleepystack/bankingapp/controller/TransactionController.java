package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.TransactionRequestForDeposit;
import com.sleepystack.bankingapp.dto.TransactionRequestForTransfer;
import com.sleepystack.bankingapp.dto.TransactionRequestForWithdrawal;
import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userPublicId}/accounts/{accountNumber}/transactions")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public Transaction deposit(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber,
            @RequestBody @Valid TransactionRequestForDeposit request) {
        log.info("Processing deposit for user: {}, account: {}, amount: {}, description: {}",
                userPublicId, accountNumber, request.getAmount(), request.getDescription());
        return transactionService.deposit(userPublicId, accountNumber, request.getAmount(), request.getDescription());
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber,
            @RequestBody @Valid TransactionRequestForWithdrawal request) {
        log.info("Processing withdrawal for user: {}, account: {}, amount: {}, description: {}",
                userPublicId, accountNumber, request.getAmount(), request.getDescription());
        return transactionService.withdrawal(userPublicId, accountNumber, request.getAmount(), request.getDescription());
    }

    @PostMapping("/transfer")
    public Transaction transfer(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber, // source account
            @RequestBody @Valid TransactionRequestForTransfer request) {
        log.info("Processing transfer for user: {}, from account: {}, to account: {}, amount: {}, description: {}",
                userPublicId, accountNumber, request.getTargetAccountNumber(), request.getAmount(), request.getDescription());
        return transactionService.transfer(userPublicId, accountNumber, request.getTargetAccountNumber(), request.getAmount(), request.getDescription());
    }

    @GetMapping
    public List<Transaction> getTransactions(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber) {
        return transactionService.getTransactionsForAccount(userPublicId, accountNumber);
    }
}