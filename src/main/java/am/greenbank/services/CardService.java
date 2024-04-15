package am.greenbank.services;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.account.AccountType;
import am.greenbank.entities.account.Currency;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.cards.CardColour;
import am.greenbank.entities.cards.CardType;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.AccountNotFoundException;
import am.greenbank.exceptions.exceptions.CardNotFoundException;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.helpers.genaretors.AccountDataGenerator;
import am.greenbank.helpers.genaretors.CardDataGenerator;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.CardRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CardDataGenerator cardDataGenerator;
    private final AccountDataGenerator accountDataGenerator;

    public Card getCardById(String cardId) {
        return cardRepository.findCardById(cardId).orElseThrow(CardNotFoundException::new);
    }

    public Card createCard(String ownerId, CardType cardType, Currency currency, CardColour colour, String accountId) {
        User user = userRepository.findUserById(ownerId).orElseThrow(UserNotFoundException::new);
        Account account;
        if (accountId == null) {
            account = createAccountForCardAndSaveIt(currency);
        } else {
            account = accountRepository.findAccountById(accountId).orElseThrow(
                AccountNotFoundException::new
            );
        }
        Card card = createCard(cardType, account, user, colour);
        List<Account> userAccounts = user.getAccounts();
        userAccounts.add(account);
        user.setAccounts(userAccounts);
        Card savedCard = cardRepository.saveCard(card);
        List<Card> userCards = user.getCards();
        userCards.add(savedCard);
        user.setCards(userCards);
        userRepository.saveUser(user);
        return savedCard;
    }

    private Card createCard(CardType cardType, Account account, User user, CardColour colour) {
        String cardNumber;

        do {
            cardNumber = cardDataGenerator.generateCardNumber(cardType);
        } while (cardRepository.findByCardNumber(cardNumber).isPresent());


        return Card.builder()
            .cardName(cardType.name())
            .cardNumber(cardNumber)
            .cardType(cardType)
            .expirationDate(cardDataGenerator.getExpirationDate())
            .account(account)
            .cardHolderFullName(user.getFirstName().toUpperCase() + " " + user.getLastName().toUpperCase())
            .cvv(cardDataGenerator.generateCVV())
            .pin(cardDataGenerator.generatePIN())
            .colour(colour)
            .build();
    }

    private Account createAccountForCardAndSaveIt(Currency currency) {
        Account account;
        String accountNumber;

        do {
            accountNumber = accountDataGenerator.generateAccountNumber();
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        Account tempAccount = Account.builder()
            .accountName("")
            .accountNumber(accountNumber)
            .balance(accountDataGenerator.generateBalance())
            .currency(currency)
            .isDefault(false)
            .accountType(AccountType.CURRENT)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        account = accountRepository.saveAccount(tempAccount);
        return account;
    }

    public Card updateCardName(String cardId, String cardName) {
        Card card = cardRepository.findCardById(cardId).orElseThrow(CardNotFoundException::new);
        card.setCardName(cardName);
        return cardRepository.saveCard(card);
    }

    public Card getCardByCardNumber(String number) {
        return cardRepository.findByCardNumber(number).orElseThrow(CardNotFoundException::new);
    }

    public void deleteById(String cardId) {
        User user = userRepository.findByCardId(cardId).orElseThrow(UserNotFoundException::new);
        List<Card> cards = user
            .getCards()
            .stream()
            .filter(card -> !card.getId().equals(cardId))
            .toList();
        user.setCards(cards);

        cardRepository.deleteById(cardId);
        userRepository.saveUser(user);
    }
}