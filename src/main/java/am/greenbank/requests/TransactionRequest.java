package am.greenbank.requests;

import am.greenbank.entities.transaction.TransactionType;
import am.greenbank.helpers.validations.TransactionToValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TransactionToValidation(message = "To field of transaction is not Valid")
public class TransactionRequest {
    @NotBlank(message = "From is required")
    @Size(min = 16, max = 16, message = "From should be a 16-digit string")
    private String from;
    @NotBlank(message = "To is required")
    private String to;
    @Positive(message = "Amount should be a positive number")
    private Double amount;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    private boolean save;
}
