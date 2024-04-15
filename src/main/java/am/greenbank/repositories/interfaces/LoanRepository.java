package am.greenbank.repositories.interfaces;

import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.loan.LoanStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Optional<Loan> findById(String id);

    Loan save(Loan loan);

    List<Loan> saveAllLoans(List<Loan> entities);

    List<Loan> findAllByLoanName(String loanName);

    List<Loan> findAllByUserId(String userId);

    List<Loan> findAll();

    List<Loan> findAllByStatus(LoanStatus status);

    List<Loan> findAllByStatusAndEndDateBefore(LoanStatus status, LocalDateTime endDate);
}
