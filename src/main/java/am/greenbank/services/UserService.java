package am.greenbank.services;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.VerificationNumber;
import am.greenbank.exceptions.exceptions.EmailNotSendException;
import am.greenbank.exceptions.exceptions.PasswordMatchException;
import am.greenbank.exceptions.exceptions.UserAlreadyExistsException;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.repositories.interfaces.*;
import am.greenbank.services.email.EmailSender;
import am.greenbank.services.email.EmailStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TemplateRepository templateRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationNumberService verificationNumberService;
    private final EmailSender emailSender;

    public User getUserById(String id) {
        return userRepository.findUserById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String id, User user) {
        Optional<User> byPhone = userRepository.findByPhone(user.getPhone());
        if (byPhone.isPresent()) {
            throw new UserAlreadyExistsException("phone");
        }

        return userRepository.findUserById(id)
            .map(userById -> {
                userById.setFirstName(Optional.ofNullable(user.getFirstName()).orElse(userById.getFirstName()));
                userById.setLastName(Optional.ofNullable(user.getLastName()).orElse(userById.getLastName()));
                userById.setBirthday(Optional.ofNullable(user.getBirthday()).orElse(userById.getBirthday()));
                userById.setPhone(Optional.ofNullable(user.getPhone()).orElse(userById.getPhone()));
                userById.setImg(Optional.ofNullable(user.getImg()).orElse(userById.getImg()));
                return userRepository.saveUser(userById);
            })
            .orElseThrow(UserNotFoundException::new);
    }

    public void deleteUser(String id) {
        User user = userRepository.findUserById(id).orElseThrow(UserNotFoundException::new);
        tokenRepository.deleteAllByUser(user);
        cardRepository.deleteAllById(
            user
                .getCards()
                .stream()
                .map(Card::getId)
                .toList()
        );
        accountRepository.deleteAllById(
            user
                .getAccounts()
                .stream()
                .map(Account::getId)
                .toList()
        );
        templateRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public void enableUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.setEnabled(true);
    }

    public User getUserByAccount(Account account) {
        return userRepository.findByAccountId(account.getId()).orElseThrow(UserNotFoundException::new);
    }

    public User saveUser(User user) {
        return userRepository.saveUser(user);
    }

    public User changePassword(String userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordMatchException("Current password doesn't match with provided old password");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMatchException("Confirm Password doesn't is not he same new password");
        }

        changePassword(newPassword, user);

        return userRepository.saveUser(user);
    }

    public void resetPassword(String userId, String password) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);

        changePassword(password, user);

        userRepository.saveUser(user);
    }

    private void changePassword(String newPassword, User user) {
        if (user.getOldPasswords() == null) {
            user.setOldPasswords(new ArrayList<>());
            user.getOldPasswords().add(user.getPassword());
        }

        List<String> oldPasswords = user.getOldPasswords();

        boolean passwordHasBeenInPrevious3Passwords = oldPasswords
            .stream()
            .anyMatch(password -> passwordEncoder.matches(newPassword, password));

        if (passwordHasBeenInPrevious3Passwords) {
            throw new PasswordMatchException("New password shouldn't match previous 3 passwords.");
        }

        if (oldPasswords.size() >= 3) {
            oldPasswords.remove(0);
        }

        oldPasswords.add(user.getPassword());

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public String changeEmailSendMessageToOldEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User with this email doesn't exists"));

        user.setCanChangeEmail(false);

        VerificationNumber number = verificationNumberService.createNumber(user);
        EmailStatus emailStatus = emailSender.sendEmail(user.getEmail(), user.getFirstName(), number.getNumber());

        switch (emailStatus) {
            case SENT -> userRepository.saveUser(user);
            case NOT_SENT -> throw new EmailNotSendException("Email doesn't send try again later");
        }

        return user.getId();
    }

    public String changeEmailSendMessageToNewEmail(String email, String userId) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userRepository.findByEmail(email)
            .ifPresent(user1 -> {
                    throw new UserAlreadyExistsException(email);
                }
            );

        VerificationNumber number = verificationNumberService.createNumber(user);
        EmailStatus emailStatus = emailSender.sendEmail(user.getEmail(), user.getFirstName(), number.getNumber());
        switch (emailStatus) {
            case SENT -> userRepository.saveUser(user);
            case NOT_SENT -> throw new EmailNotSendException("Email doesn't send try again later");
        }

        user.setFutureEmail(email);
        User savedUser = userRepository.saveUser(user);
        return savedUser.getId();
    }
}
