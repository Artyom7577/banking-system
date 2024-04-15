package am.greenbank.repositories.mongo;


import am.greenbank.entities.user.User;
import am.greenbank.repositories.interfaces.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMongoRepository extends UserRepository, MongoRepository<User, String> {
    @Override
    default Optional<User> findUserById(String id) {
        return findByIdAndDeleted(id, false);
    }

    @Override
    default void deleteById(String id) {
        Optional<User> userById = findById(id);

        if (userById.isPresent()) {
            User user = userById.get();
            user.setDeleted(true);
            saveUser(user);
        }
    }

    @Override
    default User saveUser(User user) {
        return save(user);
    }

    @Override
    default Optional<User> findByEmail(String username) {
        return findByEmailAndDeleted(username, false);
    }

    @Override
    default Optional<User> findByPhone(String phone) {
        return findByPhoneAndDeleted(phone, false);
    }

    @Override
    @Query("{'accounts.id': ?0, 'deleted': false}")
    Optional<User> findByAccountId(String accountId);

    @Override
    @Query("{'cards.id': ?0, 'deleted': false}")
    Optional<User> findByCardId(String cardId);
}
