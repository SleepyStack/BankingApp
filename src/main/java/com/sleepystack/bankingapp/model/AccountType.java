package com.sleepystack.bankingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "account_types")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class AccountType {
    @Id
    private String id;

    private String typeName;
    private String publicIdentifier;
    private String description;
}