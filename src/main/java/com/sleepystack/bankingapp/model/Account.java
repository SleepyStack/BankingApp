package com.sleepystack.bankingapp.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Account {
    @Id
    private String id;

    private String userId;
    private String accountNumber;
    private String accountTypeId;
    private double balance;
}



