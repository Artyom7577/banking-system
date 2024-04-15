package am.greenbank.repositories.interfaces;


import am.greenbank.entities.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByIdAndDeleted(String id, boolean deleted);

    Optional<User> findUserById(String id);

    List<User> findAll();

    void deleteById(String id);

    User saveUser(User user);

    Optional<User> findByEmailAndDeleted(String email, boolean deleted);

    Optional<User> findByEmail(String username);

    Optional<User> findByPhoneAndDeleted(String phone, boolean deleted);

    Optional<User> findByPhone(String phone);

    Optional<User> findByAccountId(String accountId);

    List<User> findByCreditworthinessId(String creditworthinessId);

    Optional<User> findByCardId(String cardId);
}
