package am.greenbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GreenBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenBankApplication.class, args);
    }
}
