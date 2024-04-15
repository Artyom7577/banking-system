package am.greenbank.repositories.interfaces;

import am.greenbank.entities.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByIdAndDeleted(String id, boolean deleted);

    Optional<Account> findAccountById(String id);

    List<Account> findAll();

    //    List<Account> findAllByAccountId(List<Account> ids);

    void deleteAllById(List<String> ids);

    Account saveAccount(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    void deleteById(String accountId);
}
