package am.greenbank.exceptions.exceptions;

import am.greenbank.entities.transaction.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionException extends RuntimeException {
    private final Transaction transaction;

    public TransactionException(Transaction transaction, String msg) {
        super(msg);
        this.transaction = transaction;
    }
}

