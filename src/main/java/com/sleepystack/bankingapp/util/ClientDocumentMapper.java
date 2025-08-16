package com.sleepystack.bankingapp.util;

import com.sleepystack.bankingapp.model.RegisteredClientDocument;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.stream.Collectors;

public class ClientDocumentMapper {
    public RegisteredClientDocument toDocument(RegisteredClient client) {
        RegisteredClientDocument doc = new RegisteredClientDocument();
        doc.setId(client.getId());
        doc.setClientId(client.getClientId());
        doc.setClientSecret(client.getClientSecret());
        doc.setAuthenticationMethods(
                client.getClientAuthenticationMethods()
                        .stream()
                        .map(ClientAuthenticationMethod::getValue)
                        .collect(Collectors.toList())
        );
        doc.setAuthorizationGrantTypes(
                client.getAuthorizationGrantTypes()
                        .stream()
                        .map(AuthorizationGrantType::getValue)
                        .collect(Collectors.toList())
        );
        return doc;
    }
}
