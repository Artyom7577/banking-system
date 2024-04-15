package am.greenbank.controllers;

import am.greenbank.dtos.CurrencyDto;
import am.greenbank.entities.account.Currency;
import am.greenbank.responses.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Tag(name = "Currency", description = "Fetches all the currencies.")
@RequiredArgsConstructor
public class CurrencyController {

    @GetMapping("/api/currencies")
    @Operation(description = "Fetches all the currencies.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved currencies",
                content = @Content(
                    schema = @Schema(
                        type = "Response",
                        example = """
                            {
                              "status": "success",
                              "value": [
                                {
                                  "value": "AMD",
                                  "fullName": "Armenian Dram",
                                  "symbol": "֏",
                                  "unicode": "U+058F"
                                },
                                {
                                  "value": "USD",
                                  "fullName": "United States Dollar",
                                  "symbol": "$",
                                  "unicode": "U+0024"
                                },
                                {
                                  "value": "EUR",
                                  "fullName": "Euro",
                                  "symbol": "€",
                                  "unicode": "U+20AC"
                                },
                                {
                                  "value": "RUB",
                                  "fullName": "Russian Ruble",
                                  "symbol": "₽",
                                  "unicode": "U+20BD"
                                }
                              ],
                              "message": "all currencies are returned"
                            }
                            """
                    )
                )

            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                    schema = @Schema(
                        type = "Response",
                        example = """ 
                            {
                                "status" : "error",
                                "value" : null,
                                "message" : "Internal server error"
                            }
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<Response> getAllCurrencies() {
        List<CurrencyDto> currencyDtoList = Arrays.stream(Currency.values())
            .map(currency -> new CurrencyDto(
                currency.name(),
                currency.getFullName(),
                currency.getSymbol(),
                currency.getUnicode()))
            .collect(Collectors.toList());

        if (currencyDtoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Response response = Response.getSuccessResponse(currencyDtoList, "all currencies are returned");

        return ResponseEntity.ok(response);
    }
}
