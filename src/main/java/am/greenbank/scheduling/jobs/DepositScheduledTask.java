package am.greenbank.scheduling.jobs;

import am.greenbank.entities.deposit.Deposit;
import am.greenbank.entities.deposit.DepositStatus;
import am.greenbank.helpers.util.BankUtil;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.DepositRepository;
import am.greenbank.services.DepositService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DepositScheduledTask {
    private final DepositRepository depositRepository;
    private final DepositService depositService;
    private static final Logger log = LoggerFactory.getLogger(VerificationNumberCollectionScheduledTask.class);

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void depositDaily() {
        findEndedDeposits();
        addDepositDaily();
    }

    public void addDepositDaily() {
        List<Deposit> inProgressDeposits = depositRepository.findAllByStatus(DepositStatus.IN_PROGRESS);
        int yesterday = LocalDateTime.now().minusDays(1).getDayOfYear();
        int today = LocalDateTime.now().getDayOfYear();
        inProgressDeposits.forEach(deposit -> {
            LocalDateTime startDate = deposit.getStartDate();
            LocalDateTime endDate = deposit.getEndDate();
            if (startDate.getDayOfYear() < yesterday && endDate.getDayOfYear() >= today) {
                double amount = deposit.getAmount();
                double percent = deposit.getPercent();
                amount += amount * percent / 100;
                deposit.setAmount(amount);
            }
        });

        depositRepository.saveAllDeposits(inProgressDeposits);
        SimpleDateFormat dateFormat = getSimpleDateFormat();
        log.info("Deposit percent added for {} deposits at {}", inProgressDeposits.size(), dateFormat.format(System.currentTimeMillis()));
    }

    public void findEndedDeposits() {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        List<Deposit> allByStatusAndEndDateBefore = depositRepository
            .findAllByStatusAndEndDateBefore(DepositStatus.IN_PROGRESS, now);
        allByStatusAndEndDateBefore.forEach(deposit -> {
            Deposit deposit1 = depositService.closeDeposit(deposit.getId());
        });
        depositRepository.saveAllDeposits(allByStatusAndEndDateBefore);
        SimpleDateFormat dateFormat = getSimpleDateFormat();
        log.info("Ended deposits found and updated at {}", dateFormat.format(System.currentTimeMillis()));
    }
}

