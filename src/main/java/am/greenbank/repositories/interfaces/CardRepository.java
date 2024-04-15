package am.greenbank.repositories.interfaces;

import am.greenbank.entities.cards.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepository {
    Optional<Card> findByAccountIdAndDeleted(String accountId, boolean deleted);

    Optional<Card> findByAccountId(String accountId);

    Optional<Card> findByIdAndDeleted(String cardId, boolean deleted);

    Optional<Card> findCardById(String cardId);

    Card saveCard(Card card);

    Optional<Card> findByCardNumber(String cardNumber);

    void deleteAllById(List<String> ids);

    void deleteById(String cardId);
}
