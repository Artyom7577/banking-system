package am.greenbank.exceptions.exceptions;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String msg) {
        super("user with this " + msg + " already exists");
    }
}
