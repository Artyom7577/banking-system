package am.greenbank.services;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.account.AccountType;
import am.greenbank.entities.account.Currency;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.AccountNotFoundException;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.helpers.genaretors.AccountDataGenerator;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountDataGenerator accountDataGenerator;

    public Account getAccountById(String id) {
        return accountRepository.findAccountById(id).orElseThrow(AccountNotFoundException::new);
    }

    public Account getAccountByAccountNumber(String number) {
        return accountRepository.findByAccountNumber(number).orElseThrow(AccountNotFoundException::new);
    }

    public Account createAccount(AccountType accountType, String ownerId, Currency currency) {
        Account account;
        String accountNumber;

        do {
            accountNumber = accountDataGenerator.generateAccountNumber();
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        account = Account.builder()
            .accountName("")
            .accountNumber(accountNumber)
            .accountType(accountType)
            .balance(accountDataGenerator.generateBalance())
            .currency(currency)
            .isDefault(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Account savedAccount = accountRepository.saveAccount(account);
        User user = userRepository.findUserById(ownerId).orElseThrow(UserNotFoundException::new);
        user.getAccounts().add(savedAccount);
        userRepository.saveUser(user);
        return savedAccount;
    }


    public Account updateAccountName(String accountId, String accountName) {
        Account account = accountRepository.findAccountById(accountId).orElseThrow(AccountNotFoundException::new);
        account.setAccountName(accountName);
        return accountRepository.saveAccount(account);
    }

    public Map<String, Double> getTotalBalancesByCurrency() {
        List<Account> allAccounts = accountRepository.findAll();

        return allAccounts.stream()
            .collect(Collectors.groupingBy(account -> String.valueOf(account.getCurrency()),
                Collectors.summingDouble(Account::getBalance)));

    }

    public void deleteById(String accountId) {
        User user = userRepository.findByAccountId(accountId).orElseThrow(UserNotFoundException::new);
        List<Account> accounts = user
            .getAccounts()
            .stream()
            .filter(account -> !account.getId().equals(accountId))
            .toList();
        user.setAccounts(accounts);

        accountRepository.deleteById(accountId);
        userRepository.saveUser(user);
    }
}
