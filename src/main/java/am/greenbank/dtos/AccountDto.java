package am.greenbank.dtos;

import am.greenbank.entities.account.AccountType;
import am.greenbank.entities.account.Currency;
import am.greenbank.responses.Value;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccountDto implements Value {
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name length should be between 2 and 50 characters")
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Size(min = 16, max = 16, message = "Account number length should be 16 characters")
    @Pattern(regexp = "[0-9]{16}$", message = "Phone number must be a valid Armenian phone number")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Balance must be greater than 0")
    private Double balance;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    private Boolean isDefault;

//    @NotNull(message = "Created at timestamp is required")
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
}

