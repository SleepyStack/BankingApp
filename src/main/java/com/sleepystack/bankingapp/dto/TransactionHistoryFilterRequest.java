package com.sleepystack.bankingapp.dto;

import com.sleepystack.bankingapp.enums.TransactionStatus;
import com.sleepystack.bankingapp.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TransactionHistoryFilterRequest {
    private TransactionType type;
    private TransactionStatus status;
    private Double minAmount;
    private Double maxAmount;
    private Instant startDate;
    private Instant endDate;
    private Integer page;
    private Integer size;
}
