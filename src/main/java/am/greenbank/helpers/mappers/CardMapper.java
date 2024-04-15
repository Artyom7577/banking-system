package am.greenbank.helpers.mappers;

import am.greenbank.dtos.CardDto;
import am.greenbank.entities.cards.Card;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class CardMapper {
    public CardDto mapCardToCardDto(Card card) {

        return CardDto.builder()
            .id(card.getId())
            .cardName(card.getCardName())
            .cardNumber(card.getCardNumber())
            .cardType(card.getCardType())
            .expirationDate(card.getExpirationDate())
            .accountNumber(card.getAccount().getAccountNumber())
            .cardHolderFullName(card.getCardHolderFullName())
            .cvv(card.getCvv())
            .colour(card.getColour())
            .currency(card.getAccount().getCurrency())
            .balance(card.getAccount().getBalance())
            .build();
    }

}
