package com.sleepystack.bankingapp.model;
import com.sleepystack.bankingapp.model.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "accounts")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Account {
    @Id
    private String id;

    private String userId;

    @Indexed(unique = true)
    private String accountNumber;

    private String accountTypeId;
    private double balance;
    private AccountStatus status;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}



