package am.greenbank.services.email;

public interface EmailSender {
    EmailStatus sendEmail(String to, String email);

    EmailStatus sendEmail(String to, String firstName, String number);
}
