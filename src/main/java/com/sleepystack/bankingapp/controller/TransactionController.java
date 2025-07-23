package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userPublicId}/accounts/{accountNumber}/transactions")
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
            @RequestParam double amount,
            @RequestParam(required = false) String description) {
        return transactionService.deposit(userPublicId, accountNumber, amount, description);
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber,
            @RequestParam double amount,
            @RequestParam(required = false) String description) {
        return transactionService.withdrawal(userPublicId, accountNumber, amount, description);
    }

    @PostMapping("/transfer")
    public Transaction transfer(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber, // source account
            @RequestParam String targetAccountNumber, // destination account
            @RequestParam double amount,
            @RequestParam(required = false) String description) {
        return transactionService.transfer(userPublicId, accountNumber, targetAccountNumber, amount, description);
    }
    @GetMapping
    public List<Transaction> getTransactions(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber) {
        return transactionService.getTransactionsForAccount(accountNumber);
    }
}