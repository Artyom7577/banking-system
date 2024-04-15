package am.greenbank.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {
    SUCCESS("success"),
    ERROR("error");

    private final String value;
//    ResponseStatus(String value) {
//        this.value = value;
//    }

}
