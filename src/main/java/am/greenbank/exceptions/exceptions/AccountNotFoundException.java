package am.greenbank.exceptions.exceptions;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException() {
        super("Account Not Found");
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
