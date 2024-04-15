package am.greenbank.repositories.mongo;

import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.repositories.interfaces.TransactionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMongoRepository extends TransactionRepository, MongoRepository<Transaction, String> {
}
