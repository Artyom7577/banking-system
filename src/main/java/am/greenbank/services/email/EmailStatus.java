package am.greenbank.services.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailStatus {
    SENT("sent"),
    NOT_SENT("not sent");

    private final String value;
}
