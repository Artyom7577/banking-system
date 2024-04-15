package am.greenbank.requests;

import am.greenbank.entities.account.AccountType;
import am.greenbank.entities.account.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "Owner ID must not be blank")
    private String ownerId;
    @NotNull(message = "AccountType type must not be null")
    private AccountType accountType;
    @NotNull(message = "Currency must not be null")
    private Currency currency;

}
