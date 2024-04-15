package am.greenbank.repositories.mongo;

import am.greenbank.entities.Creditworthiness;
import am.greenbank.repositories.interfaces.CreditworthinessRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditworthinessMongoRepository extends CreditworthinessRepository, MongoRepository<Creditworthiness, String> {
    @Override
    default List<Creditworthiness> saveAllCreditworthinesses(List<Creditworthiness> creditworthinesses) {
        return saveAll(creditworthinesses);
    }
}
