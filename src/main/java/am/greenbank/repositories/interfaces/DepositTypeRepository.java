package am.greenbank.repositories.interfaces;

import am.greenbank.entities.deposit.DepositType;

import java.util.List;
import java.util.Optional;

public interface DepositTypeRepository {
    Optional<DepositType> findById(String id);

    Optional<DepositType> findByName(String name);

    DepositType save(DepositType loanType);

    List<DepositType> findAll();

    List<DepositType> findAllByAvailableIsTrue();

    List<DepositType> findAllByAvailableIsFalse();

    void deleteByName(String loanName);
}
