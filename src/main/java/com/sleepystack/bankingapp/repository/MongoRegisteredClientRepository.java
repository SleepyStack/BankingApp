package com.sleepystack.bankingapp.repository;

import com.sleepystack.bankingapp.model.RegisteredClientDocument;
import com.sleepystack.bankingapp.util.ClientDocumentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MongoRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientDocumentRepository documentRepository;
    private final ClientDocumentMapper mapper = new ClientDocumentMapper();

    @Autowired
    public MongoRegisteredClientRepository(ClientDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        RegisteredClientDocument doc = mapper.toDocument(registeredClient);
        documentRepository.save(doc);
    }

    @Override
    public RegisteredClient findById(String id) {
        // TODO: implement using documentRepository.findById(...)
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        // TODO: implement using documentRepository.findByClientId(...)
        return null;
    }
}
