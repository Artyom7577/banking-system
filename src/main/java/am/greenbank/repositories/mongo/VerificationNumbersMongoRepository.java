package am.greenbank.repositories.mongo;

import am.greenbank.entities.user.VerificationNumber;
import am.greenbank.repositories.interfaces.VerificationNumbersRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationNumbersMongoRepository extends VerificationNumbersRepository,
    MongoRepository<VerificationNumber, String> {
}
