package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping
    public Transaction deposit(@RequestParam String accountId,
                               @RequestParam double amount,
                               @RequestParam String userId,
                               @RequestParam(required = false) String description) {
        return transactionService.deposit(accountId,amount,userId,description);
    }
    @PostMapping
    public Transaction withdraw(@RequestParam String accountId,
                                @RequestParam double amount,
                                @RequestParam String userId,
                                @RequestParam(required = false) String description) {
        return transactionService.withdraw(accountId, amount, userId, description);
    }
    @PostMapping
    public Transaction transfer(@RequestParam String fromAccountId,
                                @RequestParam String toAccountId,
                                @RequestParam double amount,
                                @RequestParam String userId,
                                @RequestParam(required = false) String description) {
        return transactionService.transfer(fromAccountId, toAccountId, amount, userId, description);
    }
}
