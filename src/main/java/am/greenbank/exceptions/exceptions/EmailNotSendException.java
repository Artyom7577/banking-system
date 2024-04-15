package am.greenbank.exceptions.exceptions;

public class EmailNotSendException extends RuntimeException {
    public EmailNotSendException(String msg) {
        super(msg);
    }
}
