package am.greenbank.services;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.account.AccountType;
import am.greenbank.entities.account.Currency;
import am.greenbank.entities.token.Token;
import am.greenbank.entities.token.TokenType;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.entities.user.VerificationNumber;
import am.greenbank.exceptions.exceptions.EmailNotSendException;
import am.greenbank.exceptions.exceptions.UserAlreadyExistsException;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.exceptions.exceptions.VerificationException;
import am.greenbank.helpers.genaretors.AccountDataGenerator;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.TokenRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import am.greenbank.requests.LoginRequest;
import am.greenbank.services.email.EmailSender;
import am.greenbank.services.email.EmailStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final AccountDataGenerator accountDataGenerator;
    private final VerificationNumberService verificationNumberService;
    private final UserService userService;
    //        private final ConfirmationTokenService confirmTokenService;
    private final EmailSender emailSender;

    @Value("${am.greenbank.creditworthiness.defaultId}")
    private String defaultCreditworthinessId;

    public User authenticate(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail().toLowerCase(),
            request.getPassword()
        ));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (user.getCreditworthinessId() == null) {
            user.setCreditworthinessId(defaultCreditworthinessId);
        }

        if (user.getNotifications() == null) {
            user.setNotifications(Collections.emptyList());
        }

        User userSaved = userRepository.saveUser(user);
        revokeAllUserTokens(userSaved);
        return user;
    }

    public User register(User user) {
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            if (userByEmail.get().isEnabled()) {
                throw new UserAlreadyExistsException("email");
            } else {
                throw new VerificationException(
                    "This user is registered but not verified please. Check your email to verify account"
                );
            }
        }

        Optional<User> userByPhone = userRepository.findByPhone(user.getPhone());
        if (userByPhone.isPresent()) {
            throw new UserAlreadyExistsException("phone");
        }

        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Account defaultAccount = createDefaultAccount();
        user.setAccounts(List.of(defaultAccount));
        user.setCards(Collections.emptyList());
        user.setNotifications(Collections.emptyList());
        user.setCreditworthinessId(defaultCreditworthinessId);
        User save = userRepository.saveUser(user);
        VerificationNumber number = verificationNumberService.createNumber(save);
        emailSender.sendEmail(user.getEmail(), buildEmail(user.getFirstName(), number.getNumber()));
        return save;
    }

    public String confirmToken(String number) {
        VerificationNumber verifyNumber = verificationNumberService.getNumber(number);

        LocalDateTime expiresAt = verifyNumber.getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token is already expired!");
        }

//        verificationNumberService.setConfirmedAt(number);

        userService.enableUser(verifyNumber.getUser().getEmail());

        //Returning confirmation message if the token matches
        return "Your email is confirmed. Thank you for using our service!";
    }

    private Account createDefaultAccount() {
        Account account = new Account();
        String accountNumber;

        do {
            accountNumber = accountDataGenerator.generateAccountNumber();
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        account.setIsDefault(true);
        account.setAccountName("Default name");
        account.setAccountNumber(accountNumber);
        account.setAccountType(AccountType.CURRENT);
        account.setBalance(accountDataGenerator.generateBalance());
        account.setCurrency(Currency.AMD);
        account.setCreatedAt(LocalDateTime.now());
        return accountRepository.saveAccount(account);
    }

    private void saveUserToken(User user, String refreshToken) {
        var token = Token.builder()
            .user(user)
            .token(refreshToken)
            .tokenType(TokenType.BEARER)
            .revoked(false)
            .expired(false)
            .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAllTokens(validUserTokens);
    }

    public String refreshAccessToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);

        var user = this.userRepository.findByEmail(userEmail).orElseThrow();

        return jwtService.generateToken(user);
    }

    public void logout(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);

        var validUserTokens = userRepository.findByEmail(userEmail)
            .map(
                user -> tokenRepository.findAllValidTokensByUserId(user.getId())
            ).orElse(Collections.emptyList());

        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAllTokens(validUserTokens);
    }

    public String forgetPasswordEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User with this email doesn't exists"));

        if (!user.isEnabled()) {
            throw new VerificationException(
                "This user is registered but not verified please. Check your email to verify account"
            );
        }

        user.setCanChangePassword(false);

        VerificationNumber number = verificationNumberService.createNumber(user);
        EmailStatus emailStatus = emailSender.sendEmail(user.getEmail(), buildEmail(user.getFirstName(), number.getNumber()));

        switch (emailStatus) {
            case SENT -> userRepository.saveUser(user);
            case NOT_SENT -> throw new EmailNotSendException("Email doesn't send try again later");
        }

        return user.getId();
    }

    private String buildEmail(String name, String number) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
            "\n" +
            "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
            "\n" +
            "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
            "    <tbody><tr>\n" +
            "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
            "        \n" +
            "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
            "          <tbody><tr>\n" +
            "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
            "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
            "                  <tbody><tr>\n" +
            "                    <td style=\"padding-left:10px\">\n" +
            "                  \n" +
            "                    </td>\n" +
            "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
            "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
            "                    </td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "              </a>\n" +
            "            </td>\n" +
            "          </tr>\n" +
            "        </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
            "    <tbody><tr>\n" +
            "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
            "      <td>\n" +
            "        \n" +
            "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
            "                  <tbody><tr>\n" +
            "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "\n" +
            "\n" +
            "\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
            "    <tbody><tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
            "        \n" +
            "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + " here is your Number " + number + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + "here would be ref" + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
            "\n" +
            "</div></div>";
    }
}