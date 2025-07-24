package com.sleepystack.bankingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransactionRequestForTransfer {
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank
    private String targetAccountNumber;


    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getTargetAccountNumber() { return targetAccountNumber; }
    public void setTargetAccountNumber(String targetAccountNumber) { this.targetAccountNumber = targetAccountNumber; }

}