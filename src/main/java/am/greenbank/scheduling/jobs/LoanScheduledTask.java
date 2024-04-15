package am.greenbank.scheduling.jobs;

import am.greenbank.entities.Creditworthiness;
import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.loan.LoanStatus;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.repositories.interfaces.CreditworthinessRepository;
import am.greenbank.repositories.interfaces.LoanRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanScheduledTask {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final CreditworthinessRepository creditworthinessRepository;
    private static final Logger log = LoggerFactory.getLogger(VerificationNumberCollectionScheduledTask.class);

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

        @Scheduled(cron = "0 0 0 * * *")
    private void loanDaily() {
        findEndedLoans();
        addLoanDaily();
    }

    public void addLoanDaily() {
        List<Loan> inProgressLoans = loanRepository.findAllByStatus(LoanStatus.IN_PROGRESS);
        int yesterday = LocalDateTime.now().minusDays(1).getDayOfYear();
        int today = LocalDateTime.now().getDayOfYear();
        inProgressLoans.forEach(loan -> {
            LocalDateTime startDate = loan.getStartDate();
            LocalDateTime endDate = loan.getEndDate();
            if (startDate.getDayOfYear() < yesterday && endDate.getDayOfYear() >= today) {
                double amount = loan.getAmount();
                double stayedAmount = loan.getStayedAmount();
                double dailyInterestRate = loan.getDailyInterestRate();
                if (stayedAmount == 0) {
                    loan.setStatus(LoanStatus.PAYED);
                } else {
                    stayedAmount += amount * dailyInterestRate;
                    loan.setStayedAmount(stayedAmount);
                }
            }
        });

        loanRepository.saveAllLoans(inProgressLoans);
        SimpleDateFormat dateFormat = getSimpleDateFormat();
        log.info("Loan percent added for {} loans at {}", inProgressLoans.size(), dateFormat.format(System.currentTimeMillis()));
    }

    public void findEndedLoans() {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        List<Loan> allByStatusAndEndDateBefore = loanRepository
            .findAllByStatusAndEndDateBefore(LoanStatus.IN_PROGRESS, now);
        allByStatusAndEndDateBefore.forEach(loan -> {
            if (loan.getStayedAmount() == 0) {
                loan.setStatus(LoanStatus.PAYED);
            } else {
                loan.setStatus(LoanStatus.NOT_PAYED);
            }
        });
        loanRepository.saveAllLoans(allByStatusAndEndDateBefore);
        SimpleDateFormat dateFormat = getSimpleDateFormat();
        log.info("Ended loans found and updated at {}", dateFormat.format(System.currentTimeMillis()));
    }
}
