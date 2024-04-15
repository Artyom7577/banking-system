package am.greenbank.repositories.mongo;

import am.greenbank.entities.account.Account;
import am.greenbank.repositories.interfaces.AccountRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountMongoRepository extends AccountRepository, MongoRepository<Account, String> {
    @Override
    default Optional<Account> findAccountById(String id) {
        return findByIdAndDeleted(id, false);
    }


    @Override
    default void deleteAllById(List<String> ids) {
        List<Account> allById = findAllById(ids);
        allById.forEach(account -> account.setDeleted(true));
        saveAll(allById);
    }

    @Override
    default Account saveAccount(Account account) {
        return save(account);
    }

    @Override
    default void deleteById(String accountId) {
        Optional<Account> accountById = findAccountById(accountId);

        if (accountById.isPresent()) {
            Account account = accountById.get();
            account.setDeleted(true);
            saveAccount(account);
        }
    }
}
