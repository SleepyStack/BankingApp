package com.sleepystack.bankingapp.resource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "authorities")
public class Authority {
    @Id
    private String id;
    private String name;
    
    private Set<User> users;
}
