package am.greenbank.entities.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    ACCOUNT("account"),
    CARD("card"),
    PHONE("phone"),
    QR_ACCOUNT("QRAccount"),
    QR_CARD("QRCard");

    private final String value;
}
