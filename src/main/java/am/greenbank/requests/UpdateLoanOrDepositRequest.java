package am.greenbank.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanOrDepositRequest {
    @Positive(message = "Amount should be positive number")
    private Double amount;
    @NotBlank(message = "Account number is required")
    @Size(min = 16, max = 16, message = "Account number length should be 16 characters")
    @Pattern(regexp = "[0-9]{16}$", message = "Account number must be a valid 16 digit number")
    private String from;
}
