package am.greenbank.repositories.mongo;

import am.greenbank.entities.deposit.Deposit;
import am.greenbank.repositories.interfaces.DepositRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositMongoRepository extends DepositRepository, MongoRepository<Deposit, String> {
    @Override
    default List<Deposit> saveAllDeposits(List<Deposit> deposits) {
        return saveAll(deposits);
    }
}

