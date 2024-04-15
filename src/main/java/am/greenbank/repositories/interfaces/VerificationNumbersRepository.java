package am.greenbank.repositories.interfaces;

import am.greenbank.entities.user.VerificationNumber;

import java.util.List;
import java.util.Optional;

public interface VerificationNumbersRepository {
    List<VerificationNumber> findByNumberAndUser(String number);

//    int updateConfirmedAt(String token, LocalDateTime localDateTime);

    VerificationNumber save(VerificationNumber number);

    Optional<VerificationNumber> findByNumber(String number);
    List<VerificationNumber> findAllByNumber(String number);

    void deleteByNumber(String number);

    void deleteById(String id);

    void deleteAllByUserId(String userId);

    void deleteAll();

}
