package com.sleepystack.bankingapp.controller;

import com.sleepystack.bankingapp.dto.*;
import com.sleepystack.bankingapp.model.Transaction;
import com.sleepystack.bankingapp.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userPublicId}/accounts/{accountNumber}/transactions")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private static final Logger adminAuditLogger = LoggerFactory.getLogger("adminAuditLogger");

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public Transaction deposit(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber,
            @RequestBody @Valid TransactionRequestForDeposit request) {
        log.info("Request to deposit amount: {} to account: {} for user: {}. Description: {}",
                request.getAmount(), accountNumber, userPublicId, request.getDescription());
        Transaction txn = transactionService.deposit(userPublicId, accountNumber, request.getAmount(), request.getDescription());
        log.info("Deposit completed. Transaction ID: {}", txn.getId());
        return txn;
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber,
            @RequestBody @Valid TransactionRequestForWithdrawal request) {
        log.info("Request to withdraw amount: {} from account: {} for user: {}. Description: {}",
                request.getAmount(), accountNumber, userPublicId, request.getDescription());
        Transaction txn = transactionService.withdrawal(userPublicId, accountNumber, request.getAmount(), request.getDescription());
        log.info("Withdrawal completed. Transaction ID: {}", txn.getId());
        return txn;
    }

    @PostMapping("/transfer")
    public Transaction transfer(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber, // source account
            @RequestBody @Valid TransactionRequestForTransfer request) {
        log.info("Request to transfer amount: {} from account: {} to account: {} for user: {}. Description: {}",
                request.getAmount(), accountNumber, request.getTargetAccountNumber(), userPublicId, request.getDescription());
        Transaction txn = transactionService.transfer(userPublicId, accountNumber, request.getTargetAccountNumber(), request.getAmount(), request.getDescription());
        log.info("Transfer completed. Transaction ID: {}", txn.getId());
        return txn;
    }

    @GetMapping
    public List<Transaction> getTransactions(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber) {
        log.info("Request to fetch transactions for account: {} by user: {}", accountNumber, userPublicId);
        List<Transaction> txns = transactionService.getTransactionsForAccount(userPublicId, accountNumber);
        log.info("Found {} transactions for account: {} by user: {}", txns.size(), accountNumber, userPublicId);
        return txns;
    }

    @PostMapping("/reverse")
    public Transaction reverseTransaction(@RequestBody @Valid ReverseTransactionRequest request) {
        log.info("Admin [{}] requested reversal for transaction [{}] with reason: {}", request.getAdminPublicId(), request.getTransactionId(), request.getReason());
        Transaction reversedTxn = transactionService.reverseTransaction(request.getAdminPublicId(), request.getTransactionId(), request.getReason());
        log.info("Reversal completed for transaction [{}] by admin [{}]", request.getTransactionId(), request.getAdminPublicId());
        return reversedTxn;
    }

    @PostMapping("/filter")
    public Page<Transaction> filterTransactions(
            @PathVariable String userPublicId,
            @PathVariable String accountNumber,
            @RequestBody TransactionHistoryFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(
                filterRequest.getPage() != null ? filterRequest.getPage() : 0,
                filterRequest.getSize() != null ? filterRequest.getSize() : 10
        );
        return transactionService.getFilteredTransactions(
                userPublicId,
                accountNumber,
                filterRequest.getType(),
                filterRequest.getStatus(),
                filterRequest.getMinAmount(),
                filterRequest.getMaxAmount(),
                filterRequest.getStartDate(),
                filterRequest.getEndDate(),
                pageable
        );
    }

}