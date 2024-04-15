package am.greenbank.services;

import am.greenbank.entities.token.Token;
import am.greenbank.entities.token.TokenType;
import am.greenbank.entities.user.User;
import am.greenbank.repositories.interfaces.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void saveToken(String refreshToken, User user) {
        Token token = Token.builder()
            .token(refreshToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .user(user)
            .build();

        tokenRepository.save(token);
    }
}
