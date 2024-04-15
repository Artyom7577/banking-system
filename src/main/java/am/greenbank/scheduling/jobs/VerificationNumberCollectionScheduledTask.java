package am.greenbank.scheduling.jobs;

import am.greenbank.repositories.interfaces.VerificationNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class VerificationNumberCollectionScheduledTask {

    private final VerificationNumbersRepository verificationNumbersRepository;
    private static final Logger log = LoggerFactory.getLogger(VerificationNumberCollectionScheduledTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 0 1 * ?") //
    @Async
    public void dropVerificationNumberCollection() {
        try {
            verificationNumbersRepository.deleteAll();
            log.info("VerificationNumber collection is dropped at {}", dateFormat.format(new Date()));
        } catch (Exception e) {
            log.error("Error occurred while dropping VerificationNumber collection: ", e);
        }
    }
}
