package am.greenbank.repositories.mongo;

import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.loan.LoanStatus;
import am.greenbank.repositories.interfaces.LoanRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanMongoRepository extends LoanRepository, MongoRepository<Loan, String> {
    @Override
    default List<Loan> saveAllLoans(List<Loan> loans) {
        return saveAll(loans);
    }
}
