package am.greenbank.repositories.mongo;

import am.greenbank.entities.deposit.DepositType;
import am.greenbank.repositories.interfaces.DepositTypeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositTypeMongoRepository extends DepositTypeRepository, MongoRepository<DepositType, String> {
}
