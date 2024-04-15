package am.greenbank.controllers;

import am.greenbank.dtos.TransactionDto;
import am.greenbank.entities.Notification;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.exceptions.exceptions.CustomMethodArgumentNotValidException;
import am.greenbank.exceptions.exceptions.UnsupportedValueException;
import am.greenbank.helpers.mappers.NotificationMapper;
import am.greenbank.helpers.mappers.TransactionMapper;
import am.greenbank.requests.QRType;
import am.greenbank.requests.TransactionRequest;
import am.greenbank.responses.Response;
import am.greenbank.responses.TransactionFilterResponse;
import am.greenbank.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@Controller
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction Description")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;
    private final CardService cardService;
    private final JwtService jwtService;
    private final NotificationServie notificationServie;
    private final NotificationMapper notificationMapper;

    @PostMapping("")
    @Operation(
        description = "Endpoint for transactions. We support only account to account, card to card, account to phone" +
            " number(Default account), account to QR created for account, and card to QR created for card transactions" +
            " and currencies of source and destination should be same",
        parameters = {
            @Parameter(
                name = "X-platform",
                description = "Platform name either \"ios\" or \"web\"",
                examples = {
                    @ExampleObject(
                        name = "Web browser",
                        value = "web"

                    ),
                    @ExampleObject(
                        name = "Apple smartphone",
                        value = "ios"
                    )
                },
                in = ParameterIn.HEADER
            )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request body for transaction",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TransactionRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Transaction Account to Account",
                        description = "Here is example for account to account transaction. (change account numbers)",
                        value = """
                            {
                                "from": "1345436382311342",
                                "to": "7257646312413652",
                                "amount": 500,
                                "description": "example of transaction A2A",
                                "type": "ACCOUNT"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Transaction Card to Card",
                        description = "Here is example for card to card transaction. (change card numbers)",
                        value = """
                            {
                                "from": "4345436382311342",
                                "to": "5257646312413652",
                                "amount": 500,
                                "description": "example of transaction C2C",
                                "type": "CARD"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Transaction Account to Phone Number",
                        description = "Here is example for account to phone number(to users default account) transaction. (change account and phone numbers)",
                        value = """
                            {
                                "from": "1345436382311342",
                                "to": "+37412345678",
                                "amount": 500,
                                "description": "example of transaction A2PN",
                                "type": "PHONE"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Transaction Account to QR Account",
                        description = "Here is example for account to QR created for account transaction. (change account number and QR data)",
                        value = """
                            {
                                 "from": "1345436382311342",
                                 "to": "eyJhbGciOiJIUzI1NiJ9.*.*",
                                 "amount": 500,
                                 "description": "example of transaction A2QRA",
                                 "type": "QR_ACCOUNT"
                            }
                             """
                    ),
                    @ExampleObject(
                        name = "Transaction Card to QR Card",
                        description = "Here is example for card to QR created for card transaction. (change card number and QR data)",
                        value = """
                            {
                                "from": "4345436382311342",
                                "to": "eyJhbGciOiJIUzI1NiJ9.*.*",
                                "amount": 500,
                                "description": "example of transaction C2QRC",
                                "type": "QR_CARD"
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Responses for successfully sent transaction",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Account to account transaction response",
                            description = "Here is example of response for account to account transaction.",
                            value = """ 
                                {
                                    "status": "success",
                                    "value": {
                                        "id": "65b383308520c64e88810063",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "7257646312413652",
                                            "type": "ACCOUNT"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction A2A",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Transaction successfully done"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Card to Card transaction response",
                            description = "Here is example of response for card to card transaction.",
                            value = """ 
                                {
                                    "status": "success",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "4345436382311342",
                                            "type": "CARD"
                                        },
                                        to: {
                                            "number": "5257646312413652",
                                            "type": "CARD"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction C2C",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Transaction successfully done"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Account to Phone Number transaction response",
                            description = "Here is example of response for account to phone number(to users default account) transaction.",
                            value = """ 
                                {
                                    "status": "success",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "7257646312413652",
                                            "type": "PHONE"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction A2PN",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Transaction successfully done"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Account to QR Account transaction response",
                            description = "Here is example of response for account to QR created for account transaction.",
                            value = """ 
                                {
                                    "status": "success",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "7257646312413652",
                                            "type": "QR_ACCOUNT"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction A2QRA",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Transaction successfully done"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Card to QR Card transaction response",
                            description = "Here is example of response for card to QR created for card transaction.",
                            value = """ 
                                {
                                    "status": "success",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "4345436382311342",
                                            "type": "CARD"
                                        },
                                        to: {
                                            "number": "5257646312413652",
                                            "type": "QR_CARD"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction C2QRC",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Transaction successfully done"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "User Not Found",
                            description = "when user by with phone number not found",
                            value = """
                                    {
                                    "status": "error",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "+37412345678",
                                            "type": "Phone"
                                        },
                                        "amount": 50000000,
                                        "description": "example of transaction A2PN - Denied because of User with phone number: +37412345678 not found",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "User with phone number: +37412345678 not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Account by provided phone number not found",
                            description = "when account by number not found",
                            value = """
                                {
                                    "status": "error",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "+37412345678",
                                            "type": "ACCOUNT"
                                        },
                                        "amount": 50000000,
                                        "description": "example of transaction A2A - Denied because of Default account for user with phone: +37412345678 not found",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Default account for user with phone: +37412345678 not fount"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Account by provided number not found",
                            description = "when account by number not found",
                            value = """
                                {
                                    "status": "error",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "7257646312413652",
                                            "type": "ACCOUNT"
                                        },
                                        "amount": 50000000,
                                        "description": "example of transaction A2A - Denied because of Account: 7257646312413652 not found",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Account: 7257646312413652 not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Card by provided number not found",
                            description = "when card by number not found",
                            value = """
                                    {
                                    "status": "success",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "4345436382311342",
                                            "type": "CARD"
                                        },
                                        to: {
                                            "number": "5257646312413652",
                                            "type": "CARD"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction C2C - Denied because of Card: 5257646312413652 not found",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Card: 5257646312413652 not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Insufficient funds",
                            description = "When account or card funds are not enough for transaction",
                            value = """ 
                                {
                                    "status": "error",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "7257646312413652",
                                            "type": "ACCOUNT"
                                        },
                                        "amount": 50000000,
                                        "description": "example of transaction A2A - Denied because of Insufficient funds",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Insufficient funds"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Incomplete currencies",
                            description = "When account or card that user sends money doesn't have same currency as users account or card",
                            value = """ 
                                {
                                    "status": "error",
                                    "value": {
                                        "id": "65b383308520c64e88810062",
                                        from: {
                                            "number": "1345436382311342",
                                            "type": "ACCOUNT"
                                        },
                                        to: {
                                            "number": "7257646312413652",
                                            "type": "Account"
                                        },
                                        "amount": 500,
                                        "description": "example of transaction A2A - Denied because of Incomplete currencies. Can't complete transaction from currency AMD to currency USD",
                                        "date": "2024-02-09 17:50:07",
                                        "done": true
                                    },
                                    message": "Incomplete currencies. Can't complete transaction from currency AMD to currency USD"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Validation error",
                            description = "When one or more field of request body doesn't pass validation",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Invalid credentials - Owner ID must not be blank\\CardType type must not be null\\nCurrency must not be null"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Refresh Token is not valid",
                            description = "when refresh token expired or is not valid",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Refresh token is not valid"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Unauthorized",
                            description = "when no token is provided",
                            value = """
                                  {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Unauthorized"
                                  }
                                """
                        ),
                        @ExampleObject(
                            name = "Access token is not valid",
                            description = "when access token expired or is not valid",
                            value = """
                                   {
                                        "status" : "error",
                                        "value" : null,
                                        "message" : "Access token is not valid"
                                   }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Access denied",
                            description = "when user without admin role tries to access other user's data",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "User can access to data connected to him"
                                    }
                                """
                        ),
                    }
                )
            ),

        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> createTransaction(
        @RequestBody @Valid TransactionRequest transactionRequest,
        Authentication authentication
    ) {
        Transaction transaction = transactionMapper.mapTransactionRequestToTransaction(transactionRequest);
        User principal = (User) authentication.getPrincipal();
        checkAccess(principal, transaction.getFrom());
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        Notification notificationToSender = notificationMapper.getNotificationFromTransactionForSender(savedTransaction);
        Notification notificationToReceiver = notificationMapper.getNotificationFromTransactionForReceiver(savedTransaction);
        notificationServie.sendNotification(notificationToSender, notificationToReceiver);
        TransactionDto transactionDto = transactionMapper.mapTransactionToTransactionDto(savedTransaction);
        Response response = Response.getSuccessResponse(transactionDto, "Transaction successfully done");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/QR")
    @Operation(
        description = "Endpoint for creating QR data for QR transactions",
        parameters = {
            @Parameter(
                name = "X-platform",
                description = "Platform name either \"ios\" or \"web\"",
                examples = {
                    @ExampleObject(
                        name = "Web browser",
                        value = "web"

                    ),
                    @ExampleObject(
                        name = "Apple smartphone",
                        value = "ios"
                    )
                },
                in = ParameterIn.HEADER
            ),
            @Parameter(
                name = "number",
                description = "Account or card number",
                required = true,
                examples = {
                    @ExampleObject(
                        name = "Account example",
                        value = "1345436382311342"
                    ),
                    @ExampleObject(
                        name = "Card example",
                        value = "4345436382311342"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "type",
                description = "QR type",
                required = true,
                examples = {
                    @ExampleObject(
                        name = "Account example",
                        value = "ACCOUNT"
                    ),
                    @ExampleObject(
                        name = "Card example",
                        value = "CARD"
                    )
                },
                in = ParameterIn.QUERY
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Responses for successfully encrypted QR Data",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Qr by account data response",
                            description = "Here is example of response for account to account transaction.",
                            value = """ 
                                {
                                    "status": "success",
                                    "value": "eyJhbGciOiJIUzI1NiJ9.*.*",
                                    "message": "Data successfully encrypted"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Account Not Found",
                            description = "when account by number not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Account Not Found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Card Not Found",
                            description = "when card by number not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Card Not Found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Validation error",
                            description = "When one or more field of request body doesn't pass validation",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Invalid credentials - Owner ID must not be blank\\CardType type must not be null\\nCurrency must not be null"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Refresh Token is not valid",
                            description = "when refresh token expired or is not valid",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Refresh token is not valid"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Unauthorized",
                            description = "when no token is provided",
                            value = """
                                  {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Unauthorized"
                                  }
                                """
                        ),
                        @ExampleObject(
                            name = "Access token is not valid",
                            description = "when access token expired or is not valid",
                            value = """
                                   {
                                        "status" : "error",
                                        "value" : null,
                                        "message" : "Access token is not valid"
                                   }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Access denied",
                            description = "when user without admin role tries to access other user's data",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "User can access to data connected to him"
                                    }
                                """
                        ),
                    }
                )
            ),

        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> createQRData(
        @RequestParam(name = "number") String number,
        @RequestParam(name = "type") QRType type,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        checkAccess(principal, number, type);

        String qrData = jwtService.generateQRCodeData(number, type);
        Response response = Response.getSuccessResponse(qrData, "Data successfully encrypted");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @Operation(
        description = "endpoint for getting users transaction",
        parameters = {
            @Parameter(
                name = "X-platform",
                description = "Platform name either \"ios\" or \"web\"",
                examples = {
                    @ExampleObject(
                        name = "Web browser",
                        value = "web"

                    ),
                    @ExampleObject(
                        name = "Apple smartphone",
                        value = "ios"
                    )
                },
                in = ParameterIn.HEADER
            ),
            @Parameter(
                name = "dateFrom",
                description = "date from which to filter transactions",
                examples = {
                    @ExampleObject(
                        name = "dateFrom",
                        value = "06-12-2023"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "dateTo",
                description = "date to which to filter transactions",
                examples = {
                    @ExampleObject(
                        name = "dateTo",
                        value = "24-12-2023"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "accountNumber",
                description = "account number to filter transactions based on that account number",
                examples = {
                    @ExampleObject(
                        name = "accountNumber",
                        value = "1122334455667788"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "cardNumber",
                description = "card number to filter transactions based on that card number",
                examples = {
                    @ExampleObject(
                        name = "cardNumber",
                        value = "4444555566667777"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "userId",
                description = "userId to filter transactions based on that userId",
                examples = {
                    @ExampleObject(
                        name = "userId",
                        value = "65b2c58757a3e03f8b933cfb"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "amountMin",
                description = "min amount of money to filter transactions based on that amount. Default value is 0.0",
                examples = {
                    @ExampleObject(
                        name = "amountMin",
                        value = "100.0"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "amountMax",
                description = "max amount of money to filter transactions based on that amount.",
                examples = {
                    @ExampleObject(
                        name = "amountMax",
                        value = "200.0"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "isCredit",
                description = "this parameter is for filtering transactions by if transaction is credit or debit. If it is not provided all transaction are returned base on the other filters.",
                examples = {
                    @ExampleObject(
                        name = "isCredit",
                        value = "true"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "isDone",
                description = "this parameter is for filtering transactions by transaction is done. If it is not provided all transaction are returned base on the other filters.",
                examples = {
                    @ExampleObject(
                        name = "isDone",
                        value = "false"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "description",
                description = "this parameter is for filtering transactions by description. If transactions description contains the provided description it will be returned.",
                examples = {
                    @ExampleObject(
                        name = "description",
                        value = "transaction description"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "page",
                description = "this parameter is for returning the page of transactions that is requested. default value is 1",
                examples = {
                    @ExampleObject(
                        name = "page",
                        value = "2"
                    )
                },
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "size",
                description = "this parameter is for size of page of transactions that is requested. default value is 15",
                examples = {
                    @ExampleObject(
                        name = "size",
                        value = "5"
                    )
                },
                in = ParameterIn.QUERY
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Responses for successfully filtered",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Account to account transaction response",
                            description = "Here is example of response for filter transaction. the transaction query was this: ?size=2&userId=65bd0b0b8f0f1471390d12c6&page=1",
                            value = """ 
                                "status": "success",
                                "value": {
                                        "transactions": [
                                            {
                                                "id": "65ce354a8d59a57f47f34444",
                                                "from": {
                                                    "number": "4024449196251234",
                                                    "type": "CARD"
                                                },
                                                "to": {
                                                    "number": "4326471287431234",
                                                    "type": "CARD"
                                                },
                                                "amount": 500.0,
                                                "description": "test transfer",
                                                "date": "2024-02-15 20:01:09",
                                                "done": true
                                            },
                                            {
                                                "id": "65ce353e8d59a57f47f3361231",
                                                "from": {
                                                    "number": "4024449196255678",
                                                    "type": "CARD"
                                                },
                                                "to": {
                                                    "number": "4326471287435678",
                                                    "type": "CARD"
                                                },
                                                "amount": 50.0,
                                                "description": "test transfer",
                                                "date": "2024-02-14 20:00:58",
                                                "done": true
                                            }
                                        ],
                                        "pageNo": 1,
                                        "pageSize": 2,
                                        "totalElements": 6,
                                        "totalPages": 3,
                                        "last": false
                                    },
                                    "message": "transaction filtered successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Refresh Token is not valid",
                            description = "when refresh token expired or is not valid",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Refresh token is not valid"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Unauthorized",
                            description = "when no token is provided",
                            value = """
                                  {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Unauthorized"
                                  }
                                """
                        ),
                        @ExampleObject(
                            name = "Access token is not valid",
                            description = "when access token expired or is not valid",
                            value = """
                                   {
                                        "status" : "error",
                                        "value" : null,
                                        "message" : "Access token is not valid"
                                   }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Access denied",
                            description = "when user without admin role tries to access other user's data or when there is no provided accountNumber or cardNumber or userID",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "User can access to data connected to him"
                                    }
                                """
                        ),
                    }
                )
            ),

        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> getTransactionByFilter(
        @RequestParam(name = "dateFrom", required = false) String dateFrom,
        @RequestParam(name = "dateTo", required = false) String dateTo,

        @Valid
        @RequestParam(name = "accountNumber", required = false)
        String accountNumber,
        @RequestParam(name = "cardNumber", required = false)
        String cardNumber,
        @RequestParam(name = "userId", required = false)
        String userId,

        @Valid
        @PositiveOrZero
        @RequestParam(name = "amountMin", required = false, defaultValue = "0.0")
        Double amountMin,
        @RequestParam(name = "amountMax", required = false)
        Double amountMax,
        @RequestParam(name = "isCredit", required = false)
        Boolean isCredit,
        @RequestParam(name = "isDone", required = false)
        Boolean isDone,
        @RequestParam(name = "description", required = false)
        String description,

        @Valid
        @Positive
        @RequestParam(name = "page", defaultValue = "1")
        int page,

        @Valid
        @Positive
        @RequestParam(name = "size", defaultValue = "15")
        int size,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        checkAccess(principal, accountNumber, cardNumber, userId);

        Pageable pageable = PageRequest.of(page - 1, size);
        LocalDateTime dateTimeFrom = LocalDateTime.parse(dateFrom);
        LocalDateTime dateTimeTo = LocalDateTime.parse(dateTo);
        Page<Transaction> transactionPage = transactionService.findTransactionsByCustomFilter(
            dateTimeFrom, dateTimeTo, accountNumber, cardNumber, userId,
            amountMin, amountMax, isCredit, isDone, description, pageable
        );

        TransactionFilterResponse transactionFilterResponse = TransactionFilterResponse
            .builder()
            .transactions(
                transactionPage
                    .getContent()
                    .stream()
                    .map(transactionMapper::mapTransactionToTransactionDto)
                    .toList()
            )
            .pageNo(transactionPage.getNumber() + 1)
            .pageSize(transactionPage.getSize())
            .totalElements(transactionPage.getTotalElements())
            .totalPages(transactionPage.getTotalPages())
            .last(transactionPage.isLast())
            .build();
        Response response = Response.getSuccessResponse(transactionFilterResponse, "transaction filtered successfully");
        return ResponseEntity.ok(response);
    }

    private void checkAccess(User principal, String accountNumber, String cardNumber, String userId) {
        boolean present;
        if (accountNumber != null) {
            present = principal
                .getAccounts()
                .parallelStream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNumber));
        } else if (cardNumber != null) {
            present = principal
                .getCards()
                .parallelStream()
                .anyMatch(card -> card.getCardNumber().equals(cardNumber));
        } else if (userId != null) {
            present = principal.getId().equals(userId);
        } else {
            throw new CustomMethodArgumentNotValidException("accountNumber or cardNumber or userId should be provided");
        }

        if (!present && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
    }

    private void checkAccess(User principal, TransactionEntity transactionEntity) {
        switch (transactionEntity.getType()) {
            case CARD -> checkCardAccess(principal, transactionEntity.getNumber());
            case ACCOUNT -> checkAccountAccess(principal, transactionEntity.getNumber());
            default -> throw new UnsupportedValueException("Unsupported value for from property");
        }
    }

    public void checkAccess(User principal, String number, QRType type) {
        switch (type) {
            case CARD -> checkCardAccess(principal, number);
            case ACCOUNT -> checkAccountAccess(principal, number);
        }
    }

    private void checkCardAccess(User principal, String cardNumber) {
        Card card = cardService.getCardByCardNumber(cardNumber);
        if (!principal.getCards().contains(card)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
    }

    private void checkAccountAccess(User principal, String accountNumber) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (!principal.getAccounts().contains(account)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
    }

}