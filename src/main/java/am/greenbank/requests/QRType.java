package am.greenbank.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QRType {
    CARD("card"),
    ACCOUNT("account");

    private final String value;
}
