package am.greenbank.exceptions.exceptions;

public class CardNotFoundException extends NotFoundException {

    public CardNotFoundException() {
        super("Card Not Found");
    }

    public CardNotFoundException(String msg) {
        super(msg);
    }
}
