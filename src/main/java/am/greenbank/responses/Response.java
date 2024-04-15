package am.greenbank.responses;

import am.greenbank.dtos.CardDto;
import am.greenbank.entities.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String status;
    private Object value;
    private String message;

    public static Response getSuccessResponse(String message) {
        return new ResponseBuilder()
            .status(ResponseStatus.SUCCESS.getValue())
            .message(message)
            .build();
    }

    public static Response getSuccessResponse(Object value, String message) {

        return new ResponseBuilder()
            .status(ResponseStatus.SUCCESS.getValue())
            .value(value)
            .message(message)
            .build();
    }

    public static Response getSuccessResponse(Object value) {
        return new ResponseBuilder()
            .status(ResponseStatus.SUCCESS.getValue())
            .value(value)
            .build();
    }

    public static Response getErrorResponse(String message) {
        return new ResponseBuilder()
            .status(ResponseStatus.ERROR.getValue())
            .message(message)
            .build();
    }

    public static Response getSuccessResponse(AuthenticationResponse value) {
        return new ResponseBuilder()
            .status(ResponseStatus.SUCCESS.getValue())
            .value(value)
            .build();
    }

    public static Response getErrorResponse(Object value, String message) {
        return new ResponseBuilder()
            .status(ResponseStatus.ERROR.getValue())
            .value(value)
            .message(message)
            .build();
    }
}
