package am.greenbank.dtos;

import am.greenbank.entities.account.Currency;
import am.greenbank.entities.cards.CardColour;
import am.greenbank.entities.cards.CardType;
import am.greenbank.helpers.validations.CardColourValidation;
import am.greenbank.responses.Value;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDto implements Value {
    private String id;
    @NotBlank(message = "Card name is required")
    @Size(min = 2, max = 50, message = "Card name length should be between 2 and 50 characters")
    private String cardName;
    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number length should be 16 characters")
    @Pattern(regexp = "[0-9]{16}", message = "Card number must contain only numeric characters")
    private String cardNumber;
    @NotBlank(message = "Card type is required")
    private CardType cardType;
    @NotBlank(message = "Card expiration Date is required")
    private String expirationDate;
    @NotBlank(message = "Account number is required")
    @Size(min = 16, max = 16, message = "Account number length should be 16 characters")
    @Pattern(regexp = "[0-9]{16}$", message = "Phone number must be a valid Armenian phone number")
    private String accountNumber;
    @NotBlank(message = "Card holder full name is required")
    private String cardHolderFullName;
    @NotBlank(message = "Card CVV is required")
    private String cvv;
    @CardColourValidation(message = "Card colours are not passed correctly")
    private CardColour colour;
    private Currency currency;
    private double balance;
}
