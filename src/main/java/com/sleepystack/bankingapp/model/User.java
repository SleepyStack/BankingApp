package com.sleepystack.bankingapp.model;

import com.sleepystack.bankingapp.enums.UserStatus;
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
import java.util.List;

@Document(collection = "users")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String publicIdentifier;

    private String name;

    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phone;

    private String password;
    private List<String> roles;
    private UserStatus status;
    private String lastLoginTime;
    private int loginAttempts;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
