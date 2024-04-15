package am.greenbank.entities.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    AMD("Armenian Dram", "֏", "U+058F"),
    USD("United States Dollar", "$", "U+0024"),
    EUR("Euro", "€", "U+20AC"),
    RUB("Russian Ruble", "₽", "U+20BD");

    private final String fullName;
    private final String symbol;
    private final String unicode;
}
