package am.greenbank.requests;

import am.greenbank.entities.account.Currency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepositRequest {
    @Min(value = 50000L, message = "Minimum value for deposit is 50000")
    private Double amount;
    @NotBlank(message = "userId is required")
    private String userId;
    @NotNull(message = "Currency is required")
    private Currency currency;
    @NotBlank(message = "Account number is required")
    @Size(min = 16, max = 16, message = "Account number length should be 16 characters")
    @Pattern(regexp = "[0-9]{16}$", message = "Account number must be a valid 16 digit number")
    private String from;
    @Positive(message = "Deposit percent should be positive")
    private Double percent;
    @NotBlank(message = "Deposit name number is required")
    private String depositName;
    @Min(value = 1L, message = "Minimum duration for deposit is 1 month")
    private Integer duration;
}
