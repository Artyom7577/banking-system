package am.greenbank.helpers.genaretors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;
@NoArgsConstructor
@Component
public final class MailVerificationNumberGenerator {
    public String generateNumber() {
        int length = 4;
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            accountNumber.append(digit);
        }

        return accountNumber.toString();
    }
}
