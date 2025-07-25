package com.sleepystack.bankingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class User {
    @Id
    private String id;
    private String publicIdentifier;

    private String name;
    private String email;
    private String phone;
}
