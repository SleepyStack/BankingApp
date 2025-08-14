package com.sleepystack.bankingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReverseTransactionRequest {
    @NotBlank
    private String transactionId;

    @NotBlank
    private String reason;
}