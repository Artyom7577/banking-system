package am.greenbank.helpers.util;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.account.Currency;
import am.greenbank.exceptions.exceptions.AccountNotFoundException;
import am.greenbank.repositories.interfaces.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class BankUtil {
    @Value("${am.greenbank.bank.accounts.AMD}")
    private String bankAMDAccountNumber;
    @Value("${am.greenbank.bank.accounts.USD}")
    private String bankUSDAccountNumber;
    @Value("${am.greenbank.bank.accounts.EUR}")
    private String bankEURAccountNumber;
    @Value("${am.greenbank.bank.accounts.RUB}")
    private String bankRUBAccountNumber;
    private final AccountRepository accountRepository;


    public Account getBankAccount(Currency currency) {
        return switch (currency) {
            case AMD -> accountRepository.findByAccountNumber(bankAMDAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank Account for AMD not found"));
            case USD -> accountRepository.findByAccountNumber(bankUSDAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank Account for USD not found"));
            case EUR -> accountRepository.findByAccountNumber(bankEURAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank Account for EUR not found"));
            case RUB -> accountRepository.findByAccountNumber(bankRUBAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Bank Account for RUB not found"));
        };
    }
}
