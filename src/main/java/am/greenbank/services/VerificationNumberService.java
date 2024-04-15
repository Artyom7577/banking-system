package am.greenbank.services;

import am.greenbank.entities.user.User;
import am.greenbank.entities.user.VerificationNumber;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.helpers.genaretors.MailVerificationNumberGenerator;
import am.greenbank.repositories.interfaces.UserRepository;
import am.greenbank.repositories.interfaces.VerificationNumbersRepository;
import am.greenbank.requests.VerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationNumberService {
    private final VerificationNumbersRepository verificationNumbersRepository;
    private final MailVerificationNumberGenerator mailVerificationNumberGenerator;
    private final UserRepository userRepository;

    public VerificationNumber saveVerificationNumber(VerificationNumber number) {
        return verificationNumbersRepository.save(number);
    }

    public VerificationNumber getNumber(String number) {
        return verificationNumbersRepository.findByNumber(number).orElseThrow();
    }

//    public int setConfirmedAt(String number) {
//        return verificationNumbersRepository.updateConfirmedAt(number, LocalDateTime.now());
//    }

    public VerificationNumber createNumber(User user) {
        String number = mailVerificationNumberGenerator.generateNumber();
        VerificationNumber verificationNumber = VerificationNumber
            .builder()
            .number(number)
            .expiresAt(LocalDateTime.now().plusHours(1))
            .user(user)
            .build();

        //TODO save after returning
        return saveVerificationNumber(verificationNumber);
    }


    public VerificationNumber verifyNumber(VerifyRequest verifyRequest) {
        VerificationNumber verifyNumber = getVerificationNumber(verifyRequest.getUserId(), verifyRequest.getNumber());
        String stringNumber = verifyNumber.getNumber();
        LocalDateTime expiresAt = verifyNumber.getExpiresAt();
        checkExpiration(expiresAt, stringNumber);
        enableUser(verifyNumber);
        verificationNumbersRepository.deleteById(verifyNumber.getId());
        return verifyNumber;
    }

    public String verifyNumberForForgetPassword(String userId, String number) {
        VerificationNumber verificationNumber = getVerificationNumber(userId, number);

        LocalDateTime expiresAt = verificationNumber.getExpiresAt();
        checkExpiration(expiresAt, number);

        String savedUserId = enableCanChangePassword(userId);

        verificationNumbersRepository.deleteById(verificationNumber.getId());

        return savedUserId;
    }

    public String verifyNumberSentToOldEmailInChangeEmail(String userId, String number) {
        VerificationNumber verificationNumber = getVerificationNumber(userId, number);

        LocalDateTime expiresAt = verificationNumber.getExpiresAt();
        checkExpiration(expiresAt, number);

        String savedUserId = enableCanChangeEmail(userId);

        verificationNumbersRepository.deleteById(verificationNumber.getId());

        return savedUserId;
    }

    public String verifyNumberSentToNewEmailInChangeEmailAndUpdateUserEmail(String userId, String number, String newEmail) {
        VerificationNumber verificationNumber = getVerificationNumber(userId, number);

        LocalDateTime expiresAt = verificationNumber.getExpiresAt();
        checkExpiration(expiresAt, number);

        String savedUserId = changeEmail(userId, newEmail);

        verificationNumbersRepository.deleteById(verificationNumber.getId());

        return savedUserId;
    }

    private String changeEmail(String userId, String newEmail) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);

        if (!user.getFutureEmail().equals(newEmail)) {
            throw new EmailNotMatchException("The email is not same that you provided before");
        }

        user.setEmail(newEmail);
        user.setFutureEmail(null);
        user.setCanChangeEmail(false);

        return userRepository.saveUser(user).getId();
    }

    private String enableCanChangeEmail(String userId) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);
        user.setCanChangeEmail(true);

        return userRepository.saveUser(user).getId();
    }

    private void enableUser(VerificationNumber verifyNumber) {
        User user = userRepository.findUserById(verifyNumber.getUser().getId()).orElseThrow(UserNotFoundException::new);
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.saveUser(user);
        }
    }

    private String enableCanChangePassword(String userId) {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);
        user.setCanChangePassword(true);

        return userRepository.saveUser(user).getId();
    }

    private void checkExpiration(LocalDateTime expiresAt, String stringNumber) {
        if (expiresAt.isBefore(LocalDateTime.now())) {
            verificationNumbersRepository.deleteByNumber(stringNumber);
            throw new VerificationException("Number has already expired!");
        }
    }

    public void deleteNumbersByUserId(String userId) {
        verificationNumbersRepository.deleteAllByUserId(userId);
    }

    private VerificationNumber getVerificationNumber(String userId, String number) {
        return verificationNumbersRepository
            .findAllByNumber(number)
            .stream()
            .filter(
                verifyNumberInStream ->
                    verifyNumberInStream
                        .getUser()
                        .getId()
                        .equals(userId)
            )
            .findFirst()
            .orElseThrow(() -> new VerificationNumberNotFoundException("Verification number not found"));
    }
}
