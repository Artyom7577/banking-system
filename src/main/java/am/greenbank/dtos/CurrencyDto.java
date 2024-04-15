package am.greenbank.dtos;

import am.greenbank.responses.Value;

public record CurrencyDto(String value, String fullName, String symbol, String unicode) implements Value {
}
