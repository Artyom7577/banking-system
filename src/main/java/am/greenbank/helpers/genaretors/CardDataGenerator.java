package am.greenbank.helpers.genaretors;

import am.greenbank.entities.cards.CardType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@NoArgsConstructor
@Component
public final class CardDataGenerator {
    public String generatePIN() {
        Random random = new Random();
        int pinNumber = random.nextInt(9000) + 1000;

        return String.format("%04d", pinNumber);
    }

    public String generateCVV() {
        Random random = new Random();
        int cvvNumber = random.nextInt(900) + 100;

        return String.valueOf(cvvNumber);
    }

    public String getExpirationDate() {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = currentDate.plusYears(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        return expiryDate.format(formatter);
    }


    public String generateCardNumber(CardType cardType) {
        int firstNumber = switch (cardType) {
            case VISA -> 4;
            case MASTERCARD -> 5;
        };

        int length = 15;

        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        cardNumber.append(firstNumber);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            cardNumber.append(digit);
        }

        return cardNumber.toString();
    }
}
