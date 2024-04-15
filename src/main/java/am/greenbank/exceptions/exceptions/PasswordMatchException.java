package am.greenbank.exceptions.exceptions;

public class PasswordMatchException extends RuntimeException {
    public PasswordMatchException() {
        super();
    }

    public PasswordMatchException(String msg) {
        super(msg);
    }
}
