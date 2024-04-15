package am.greenbank.repositories.interfaces;

import am.greenbank.entities.token.Token;
import am.greenbank.entities.user.User;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    List<Token> findAllValidTokensByUserId(String userId);

    Optional<Token> findByToken(String token);

    List<Token> saveAllTokens(List<Token> entities);

    void deleteAllByUser(User user);

    Token save(Token token);
}
