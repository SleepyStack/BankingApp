package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userPublicId}/accounts/{accountNumber}/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping("/deposit")
    public Transaction deposit( @PathVariable String accountNumber,
                                @RequestParam double amount,
                                @PathVariable String userPublicId,
                                @RequestParam(required = false) String description) {
        return transactionService.deposit(accountNumber,amount,userPublicId,description);
    }
    @PostMapping("/withdraw")
    public Transaction withdraw(@PathVariable String accountNumber,
                                @RequestParam double amount,
                                @PathVariable String userPublicId,
                                @RequestParam(required = false) String description) {
        return transactionService.withdraw(accountNumber, amount, userPublicId, description);
    }
    @PostMapping("/transfer")
    public Transaction transfer(@PathVariable String accountNumber,
                                @RequestParam String toAccountId,
                                @RequestParam double amount,
                                @PathVariable String userPublicId,
                                @RequestParam(required = false) String description) {
        return transactionService.transfer(accountNumber, toAccountId, amount, userPublicId, description);
    }
}
