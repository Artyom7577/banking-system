package am.greenbank.exceptions.exceptions;

public abstract class InUseException extends RuntimeException {
    public InUseException(String message) {
        super(message);
    }
}
