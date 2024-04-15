package am.greenbank.config;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.account.AccountType;
import am.greenbank.entities.account.Currency;
import am.greenbank.repositories.interfaces.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BankAccountCreator implements CommandLineRunner {
    private final AccountRepository accountRepository;

    @Value("${am.greenbank.bank.accounts.AMD}")
    private String bankAccountForAMD;
    @Value("${am.greenbank.bank.accounts.USD}")
    private String bankAccountForUSD;
    @Value("${am.greenbank.bank.accounts.EUR}")
    private String bankAccountForEUR;
    @Value("${am.greenbank.bank.accounts.RUB}")
    private String bankAccountForRUB;

    @Override
    public void run(String... args) throws Exception {
        createBankAccountIfNotExists(bankAccountForAMD, Currency.AMD);
        createBankAccountIfNotExists(bankAccountForUSD, Currency.USD);
        createBankAccountIfNotExists(bankAccountForEUR, Currency.EUR);
        createBankAccountIfNotExists(bankAccountForRUB, Currency.RUB);
    }

    private void createBankAccountIfNotExists(String accountNumber, Currency currency) {
        int year = 2024;
        int month = 1;
        int day = 1;
        int hour = 0;
        int minute = 0;

        if (accountRepository.findByAccountNumber(accountNumber).isEmpty()) {
            Account account = Account.builder()
                .accountName("Bank account for " + currency)
                .accountNumber(accountNumber)
                .balance(999_999_999_999.0)
                .currency(currency)
                .isDefault(true)
                .accountType(AccountType.CURRENT)
                .createdAt(LocalDateTime.of(year, month, day, hour, minute))
                .updatedAt(LocalDateTime.of(year, month, day, hour, minute))
                .build();

            accountRepository.saveAccount(account);
        }
    }
}
