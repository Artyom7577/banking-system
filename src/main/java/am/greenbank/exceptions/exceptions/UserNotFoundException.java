package am.greenbank.exceptions.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("User Not Found");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
