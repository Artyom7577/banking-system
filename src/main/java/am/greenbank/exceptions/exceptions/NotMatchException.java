package am.greenbank.exceptions.exceptions;

public abstract class NotMatchException extends RuntimeException {
    public NotMatchException(String message) {
        super(message);
    }
}
