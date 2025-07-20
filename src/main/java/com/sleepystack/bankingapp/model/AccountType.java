package com.sleepystack.bankingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "account_types")
public class AccountType {
    @Id
    private String id;

    private String typeName;
    private String description;

    public AccountType() {}

    public AccountType(String id, String typeName, String description) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}