package am.greenbank.repositories.interfaces;

import am.greenbank.entities.loan.LoanType;

import java.util.List;
import java.util.Optional;

public interface LoanTypeRepository {
    Optional<LoanType> findById(String id);

    Optional<LoanType> findByName(String name);

    LoanType save(LoanType loanType);

    List<LoanType> findAll();

    List<LoanType> findAllByAvailableIsTrue();

    List<LoanType> findAllByAvailableIsFalse();

    void deleteByName(String loanName);
}
