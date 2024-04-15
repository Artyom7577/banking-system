package am.greenbank.repositories.mongo;

import am.greenbank.entities.token.Token;
import am.greenbank.repositories.interfaces.TokenRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenMongoRepository extends TokenRepository, MongoRepository<Token, String> {
    @Query("{'user.id': ?0, $or: [{'expired': false}, {'revoked': false}] }")
    List<Token> findAllValidTokensByUserId(String userId);

    @Override
    default List<Token> saveAllTokens(List<Token> entities) {
        return saveAll(entities);
    }
}
