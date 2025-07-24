package com.sleepystack.bankingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAccountRequest {
    @NotBlank(message = "Account type identifier is required")
    private String accountTypePublicIdentifier;

    @NotNull(message = "Initial balance is required")
    @Min(value = 0, message = "Initial balance must be non-negative")
    private Double initialBalance;

    public String getAccountTypePublicIdentifier() { return accountTypePublicIdentifier; }
    public void setAccountTypePublicIdentifier(String accountTypePublicIdentifier) { this.accountTypePublicIdentifier = accountTypePublicIdentifier; }

    public Double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(Double initialBalance) { this.initialBalance = initialBalance; }
}