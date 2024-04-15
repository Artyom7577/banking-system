package am.greenbank.helpers.mappers;

import am.greenbank.dtos.AccountDto;
import am.greenbank.entities.account.Account;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class AccountMapper {
    public AccountDto mapAccountToAccountDto(Account account) {
        return AccountDto.builder()
            .id(account.getId())
            .accountName(account.getAccountName())
            .accountNumber(account.getAccountNumber())
            .accountType(account.getAccountType())
            .balance(account.getBalance())
            .currency(account.getCurrency())
            .isDefault(account.getIsDefault())
            .build();
    }

    private Account mapAccountDtoToAccount(AccountDto accountDto) {
        return Account.builder()
            .id(accountDto.getId())
            .accountName(accountDto.getAccountName())
            .accountNumber(accountDto.getAccountNumber())
            .balance(accountDto.getBalance())
            .currency(accountDto.getCurrency())
            .isDefault(accountDto.getIsDefault())
            .accountType(accountDto.getAccountType())
            .build();
    }
}
