package com.sleepystack.bankingapp.common.dto;

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