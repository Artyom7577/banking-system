package am.greenbank.entities.cards;

import am.greenbank.entities.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "card")
public class Card {
    @Id
    private String id;
    private String cardName;
    private String cardNumber;
    private CardType cardType;
    private String expirationDate;
    @DBRef
    private Account account;
    private String cardHolderFullName;
    private String cvv;
    private String pin;
    private CardColour colour;
    private boolean deleted;
}
