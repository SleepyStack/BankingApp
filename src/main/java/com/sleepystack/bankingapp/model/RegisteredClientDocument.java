package com.sleepystack.bankingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "oauth2_registered_clients")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisteredClientDocument {

    @Id
    private String id;

    private String clientId;
    private String clientSecret;
    private List<String> authenticationMethods;
    private List<String> authorizationGrantTypes;
    private List<String> redirectUris;
    private List<String> postLogoutRedirectUris;
    private List<String> scopes;
    private boolean requireAuthorizationConsent;
}