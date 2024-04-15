package am.greenbank.repositories.interfaces;

import am.greenbank.entities.Creditworthiness;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface CreditworthinessRepository {
    Optional<Creditworthiness> findById(String id);

    Optional<Creditworthiness> findByOrder(int order);

    Creditworthiness save(Creditworthiness creditworthiness);

    List<Creditworthiness> findAll();

    List<Creditworthiness> findAll(Sort sort);

    Optional<Creditworthiness> findByName(String name);

    List<Creditworthiness> saveAllCreditworthinesses(List<Creditworthiness> creditworthinesses);

    void deleteById(String creditworthinessId);
}
