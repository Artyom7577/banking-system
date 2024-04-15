package am.greenbank.exceptions.exceptions;

public class CurrencyNotMatchException extends NotMatchException {
    public CurrencyNotMatchException() {
        super("Currency not match");
    }

    public CurrencyNotMatchException(String message) {
        super(message);
    }
}
