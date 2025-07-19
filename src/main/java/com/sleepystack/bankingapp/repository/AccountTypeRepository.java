package com.sleepystack.bankingapp.repository;
import com.sleepystack.bankingapp.model.AccountType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends MongoRepository<AccountType, String> {
}
