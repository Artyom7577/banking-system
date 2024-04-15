package am.greenbank.helpers.validations;

import am.greenbank.entities.transaction.TransactionType;
import am.greenbank.requests.TransactionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransactionToValidationValidator implements ConstraintValidator<TransactionToValidation, TransactionRequest> {
    private String message;
    @Override
    public void initialize(TransactionToValidation constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(TransactionRequest transactionRequest, ConstraintValidatorContext context) {
        if (!isValidToField(transactionRequest)) {
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("to")
                .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isValidToField(TransactionRequest transactionRequest) {
        return isToValidForCardOrAccount(transactionRequest) ||
            isToValidForPhone(transactionRequest) ||
            isToValidForQR(transactionRequest);
    }

    private boolean isToValidForCardOrAccount(TransactionRequest transactionRequest) {
        return (transactionRequest.getType() == TransactionType.CARD
            || transactionRequest.getType() == TransactionType.ACCOUNT)
            && transactionRequest.getTo().matches("\\d{16}");
    }

    private boolean isToValidForPhone(TransactionRequest transactionRequest) {
        return transactionRequest.getType() == TransactionType.PHONE
            && transactionRequest.getTo().matches("^\\+374[0-9]{8}$");
    }

    private boolean isToValidForQR(TransactionRequest transactionRequest) {
        return (transactionRequest.getType() == TransactionType.QR_ACCOUNT
            || transactionRequest.getType() == TransactionType.QR_CARD)
            && transactionRequest.getTo().matches("[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+");
    }
}
