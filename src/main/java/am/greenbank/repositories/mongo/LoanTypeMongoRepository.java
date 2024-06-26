package am.greenbank.repositories.mongo;

import am.greenbank.entities.loan.LoanType;
import am.greenbank.repositories.interfaces.LoanTypeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanTypeMongoRepository extends LoanTypeRepository, MongoRepository<LoanType, String> {
}
