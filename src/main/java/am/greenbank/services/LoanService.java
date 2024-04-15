package am.greenbank.services;

import am.greenbank.entities.Creditworthiness;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.loan.LoanStatus;
import am.greenbank.entities.loan.LoanType;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.entities.transaction.TransactionType;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.helpers.util.BankUtil;
import am.greenbank.repositories.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationServie notificationServie;
    private final CreditworthinessRepository creditworthinessRepository;
    private final BankUtil bankUtil;

    public Loan createLoan(Loan loan, String toAccountNumber) {
        Account userAccount = accountRepository.findByAccountNumber(toAccountNumber).orElseThrow(AccountNotFoundException::new);
        if (userAccount.getCurrency() != loan.getCurrency()) {
            throw new CurrencyNotMatchException();
        }
        LoanType loanType = loanTypeRepository.findByName(loan.getLoanName()).orElseThrow(
            () -> new LoanTypeNotFoundException("Loan type not found")
        );
        boolean optionExists = loanType.getOptions()
            .stream()
            .anyMatch(
                loanTypeOption ->
                    loanTypeOption.getDuration().equals(loan.getDuration()) &&
                        loanTypeOption.getPercent().equals(loan.getPercent())
            );
        if (!optionExists) {
            throw new OptionNotFoundException("This option for this loan type doesn't exist");
        }

        Account bankAccount = bankUtil.getBankAccount(loan.getCurrency());
        Double loanAmount = loan.getAmount();
        bankAccount.setBalance(bankAccount.getBalance() - loanAmount);
        userAccount.setBalance(userAccount.getBalance() + loanAmount);
        Integer duration = loan.getDuration();
        Double percent = loan.getPercent();
        double dailyInterestRate = percent / 100 / 30;
        double payment = (loanAmount / (duration * 30)) + (loanAmount * dailyInterestRate);
        LocalDateTime loanStart = LocalDateTime.now();
        loan.setStartDate(loanStart);
        loan.setEndDate(loanStart.plusMonths(loan.getDuration()));
        loan.setPayment(payment);
        loan.setStatus(LoanStatus.IN_PROGRESS);
        loan.setStayedAmount(loanAmount);
        loan.setDailyInterestRate(dailyInterestRate);

        accountRepository.saveAccount(userAccount);
        accountRepository.saveAccount(bankAccount);
        return loanRepository.save(loan);
    }

    public List<Loan> getLoansByUserId(String userId) {
        return loanRepository.findAllByUserId(userId);
    }

    public Loan payLoan(String loanId, Double amount, String fromAccountNumber) {
        Account userAccount = accountRepository.findByAccountNumber(fromAccountNumber).orElseThrow(AccountNotFoundException::new);
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));
        if (loan.getStayedAmount() == 0) {
            throw new LoanException("Loan is already paid");
        }
        if (userAccount.getCurrency() != loan.getCurrency()) {
            throw new CurrencyNotMatchException(
                "Currency not match loan is " + loan.getCurrency() + " your account is " + userAccount.getCurrency()
            );
        }
        if (amount < loan.getPayment()) {
            throw new MinimalPaymentAmountException("Minimal amount for this loan payment is" + loan.getPayment());
        }
        Account bankAccount = bankUtil.getBankAccount(loan.getCurrency());
        if (loan.getStayedAmount() < amount) {
            amount = loan.getStayedAmount();
        }

        Transaction transaction = Transaction
            .builder()
            .from(
                TransactionEntity
                    .builder()
                    .number(userAccount.getAccountNumber())
                    .userId(loan.getUserId())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .to(
                TransactionEntity
                    .builder()
                    .number(bankAccount.getAccountNumber())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .amount(amount)
            .description("Paying loan " + loan.getLoanName())
            .date(LocalDateTime.now())
            .done(true)
            .currency(loan.getCurrency())
            .build();

        if (userAccount.getBalance() < amount) {
            throw new TransactionException(transaction, "Insufficient funds");
        }

        loan.setStayedAmount(loan.getStayedAmount() - amount);
        userAccount.setBalance(userAccount.getBalance() - amount);
        bankAccount.setBalance(bankAccount.getBalance() + amount);

        if (loan.getStayedAmount() == 0) {
            loan.setStatus(LoanStatus.PAYED);
        }

        accountRepository.saveAccount(userAccount);
        accountRepository.saveAccount(bankAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);
        loan.getPaymentDays().add(LocalDate.from(savedTransaction.getDate()));
        return loanRepository.save(loan);
    }

    public Loan getLoan(String loanId) {
        return loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));
    }

    public boolean canGiveLoanToUser(String userId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "order");
        List<Creditworthiness> creditworthinesses = creditworthinessRepository.findAll(sort);
        List<Loan> allByUserId = loanRepository.findAllByUserId(userId);
        if (allByUserId.isEmpty()) {
            return true;
        }

        boolean hasNotPayedLoan = allByUserId
            .stream()
            .anyMatch(loan -> loan.getStatus() == LoanStatus.NOT_PAYED);

        if (hasNotPayedLoan) {
            return creditworthinesses.get(creditworthinesses.size() - 1).isCanGetLoan();
        }

        //TODO BETTER checking
        int missDaysCount = 0;
        int inProgressLoansCount = 0;
        int payedLoansCount = 0;
        int successfullyPayedLoansCount = 0;
        int today = LocalDateTime.now().getDayOfYear();
        for (Loan loan : allByUserId) {
            if (loan.getStatus() == LoanStatus.IN_PROGRESS) {
                inProgressLoansCount++;
                int startDay = loan.getStartDate().getDayOfYear();
                if (startDay != today) {
                    missDaysCount += Math.abs((today - startDay) - loan.getPaymentDays().size());
                }
            } else if (loan.getStatus() == LoanStatus.PAYED) {
                payedLoansCount++;
                List<LocalDate> paymentDays = loan.getPaymentDays();
                LocalDate lastPaymentDay = paymentDays.get(paymentDays.size() - 1);
                if (loan.getEndDate().isBefore(lastPaymentDay.atStartOfDay())) {
                    if (today - lastPaymentDay.getDayOfYear() >= 10) {
                        successfullyPayedLoansCount++;
                    }
                } else if (lastPaymentDay.atStartOfDay().isBefore(loan.getEndDate()) || lastPaymentDay.atStartOfDay().isEqual(loan.getEndDate())) {
                    successfullyPayedLoansCount++;
                }
            }
        }

        boolean bool1 = payedLoansCount == successfullyPayedLoansCount;
        boolean bool2 = missDaysCount <= inProgressLoansCount;
        return bool1 && bool2;
    }
}
