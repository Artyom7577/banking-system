package am.greenbank.repositories.mongo;

import am.greenbank.entities.cards.Card;
import am.greenbank.repositories.interfaces.CardRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardMongoRepository extends CardRepository, MongoRepository<Card, String> {
    @Override
    default Optional<Card> findByAccountId(String accountId) {
        return findByAccountIdAndDeleted(accountId, false);
    }

    @Override
    default Optional<Card> findCardById(String cardId) {
        return findByIdAndDeleted(cardId, false);
    }

    @Override
    default Card saveCard(Card card) {
        return save(card);
    }

    @Override
    default void deleteAllById(List<String> ids) {
        List<Card> cardsById = findAllById(ids);
        cardsById.forEach(card -> card.setDeleted(true));
        saveAll(cardsById);
    }

    @Override
    default void deleteById(String cardId) {
        Optional<Card> cardById = findCardById(cardId);
        if (cardById.isPresent()) {
            Card card = cardById.get();
            card.setDeleted(true);
            save(card);
        }
    }
}
