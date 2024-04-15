package am.greenbank.repositories.interfaces;

import am.greenbank.entities.deposit.Deposit;
import am.greenbank.entities.deposit.DepositStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DepositRepository {
    Optional<Deposit> findById(String id);

    Deposit save(Deposit deposit);

    List<Deposit> saveAllDeposits(List<Deposit> entities);

    List<Deposit> findAllByDepositName(String depositName);

    List<Deposit> findAllByUserId(String userId);

    List<Deposit> findAll();

    List<Deposit> findAllByStatus(DepositStatus status);

    List<Deposit> findAllByStatusAndEndDateBefore(DepositStatus status, LocalDateTime endDate);
}
