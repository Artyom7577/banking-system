package am.greenbank.helpers.genaretors;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Random;

@Component
@RequiredArgsConstructor
public final class AccountDataGenerator {
    private final DecimalFormat decimalFormat;

    public Double generateBalance() {
        Random random = new Random();
        int min = 0;
        int max = 10000;
        double balance = min + (max - min) * random.nextDouble();
        String formattedNumber = decimalFormat.format(balance);
        return Double.parseDouble(formattedNumber);
    }

    public String generateAccountNumber() {
        int length = 16;
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            accountNumber.append(digit);
        }

        return accountNumber.toString();
    }


}
