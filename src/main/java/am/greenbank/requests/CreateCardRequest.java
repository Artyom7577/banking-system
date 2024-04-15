package am.greenbank.requests;

import am.greenbank.entities.account.Currency;
import am.greenbank.entities.cards.CardColour;
import am.greenbank.entities.cards.CardType;
import am.greenbank.helpers.validations.CardColourValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCardRequest {
    @NotBlank(message = "Owner ID must not be blank")
    private String ownerId;
    @NotNull(message = "Card type must not be null")
    private CardType cardType;
    @NotNull(message = "Currency must not be null")
    private Currency currency;
    @CardColourValidation(message = "Invalid colour syntax")
    private CardColour colour;
}