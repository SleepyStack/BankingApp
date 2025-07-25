package com.sleepystack.bankingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateAccountRequest {
    @NotNull(message = "Initial balance is required")
    @Min(value = 0, message = "Initial balance must be non-negative")
    private Double initialBalance;
}