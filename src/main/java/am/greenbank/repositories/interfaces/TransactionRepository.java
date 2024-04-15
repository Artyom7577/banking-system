package am.greenbank.repositories.interfaces;

import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.entities.user.User;

import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction user);

    Optional<Transaction> findById(String id);

    void deleteById(String id);
}
