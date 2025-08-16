package com.sleepystack.bankingapp.repository;

import com.sleepystack.bankingapp.model.RegisteredClientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDocumentRepository
        extends MongoRepository<RegisteredClientDocument, String> {

    Optional<RegisteredClientDocument> findByClientId(String clientId);
}
