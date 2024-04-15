package am.greenbank.controllers;

import am.greenbank.dtos.CardDto;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.exceptions.exceptions.CardNotFoundException;
import am.greenbank.helpers.mappers.CardMapper;
import am.greenbank.requests.CreateAccountRequest;
import am.greenbank.requests.CreateCardRequest;
import am.greenbank.requests.UpdateNameRequest;
import am.greenbank.responses.Response;
import am.greenbank.services.CardService;
import am.greenbank.services.UserService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Card Description")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final UserService userService;
    private final CardMapper cardMapper;

    @GetMapping("/{cardId}")
    @Operation(
        description = "Fetches the details of a specific account.",
        parameters = {
            @Parameter(
                name = "cardId",
                description = "card id to update(chang id, it is random number)",
                examples = {
                    @ExampleObject(
                        name = "card id",
                        value = "642s143142f5g24232653b2"
                    )
                },
                in = ParameterIn.PATH
            ),
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Card successfully returned",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                    {
                                      "status": "success",
                                      "value": {
                                        "id": "65b383308520c64e88810063",
                                        "cardName": "",
                                        "cardNumber": "4934393881112354",
                                        "cardType": "VISA",
                                        "expirationDate": "01/29",
                                        "accountNumber": "6830914089245145",
                                        "cardHolderFullName": "CLARK KENT",
                                        "cvv": "942",
                                        "colour" : {
                                            "firstHex": "#02F0BE",
                                            "secondHex": "#05C29B"
                                        },
                                        "currency": "AMD",
                                        "balance": 150000.32
                                      },
                                      "message": "card created"
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
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Card Not Found",
                            description = "when user by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Card Not Found"
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
    public ResponseEntity<Response> getCardById(@PathVariable String cardId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();

        if (principal.getCards() == null || principal.getCards().isEmpty()) {
            throw new CardNotFoundException();
        }

        Card cardById = cardService.getCardById(cardId);

        if (!principal.getRole().equals(UserRole.ADMIN) && !principal.getCards().contains(cardById)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        CardDto cardDto = cardMapper.mapCardToCardDto(cardById);
        Response response = Response.getSuccessResponse(cardDto, "card by id returned");

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = {"/", "/{accountId}"})
    @Operation(
        description = "Create a new account.",
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
            description = "This is request body for user data update",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateCardRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Create Account",
                        description = "create account request body. (Change id it is random)",
                        value = """
                            {
                              "ownerId": "6fhjifg25fg27tw135142v453",
                              "cardType": "Visa",
                              "currency": "AMD",
                              "colour" : {
                                "firstHex": "#02F0BE",
                                "secondHex": "#05C29B"
                              }
                            }
                            """
                    )
                }
            )
        ),

        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Successfully card created",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                    {
                                      "status": "success",
                                      "value": {
                                        "id": "65b383308520c64e88810063",
                                        "cardName": "",
                                        "cardNumber": "4934393881112354",
                                        "cardType": "VISA",
                                        "expirationDate": "01/29",
                                        "accountNumber": "6830914089245145",
                                        "cardHolderFullName": "CLARK KENT",
                                        "cvv": "942",
                                        "colour" : {
                                            "firstHex": "#02F0BE",
                                            "secondHex": "#05C29B"
                                        },
                                        "currency": "AMD",
                                        "balance": 150000.32
                                        
                                      },
                                      "message": "card created"
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
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "User Not Found",
                            description = "when user by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "User Not Found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Account Not Found",
                            description = "when account by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Account Not Found"
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

    public ResponseEntity<Response> createCard(
        @PathVariable(required = false) String accountId,
        @RequestBody @Valid CreateCardRequest createCardRequest,
        Authentication authentication
    ) {

        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(createCardRequest.getOwnerId()) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Card card = cardService.createCard(
            createCardRequest.getOwnerId(),
            createCardRequest.getCardType(),
            createCardRequest.getCurrency(),
            createCardRequest.getColour(),
            accountId
        );
        CardDto cardDto = cardMapper.mapCardToCardDto(card);
        Response response = Response.getSuccessResponse(cardDto, "card created");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/all/{userId}")
    @Operation(
        description = "Endpoint for get all specific User Accounts",
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Card Successfully returned",
                content = @Content(
                    schema = @Schema(
                        implementation = List.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status": "success",
                                      "value": [
                                          {
                                            "id": "65b383308520c64e88810063",
                                            "cardName": "",
                                            "cardNumber": "4934393881112354",
                                            "cardType": "VISA",
                                            "expirationDate": "01/29",
                                            "accountNumber": "6830914089245145",
                                            "cardHolderFullName": "CLARK KENT",
                                            "cvv": "942",
                                            "colour" : {
                                                "firstHex": "#02F0BE",
                                                "secondHex": "#05C29B"
                                            },
                                            "currency": "AMD",
                                            "balance": 150000.32
                                          },
                                      ]
                                      "message": "card created"
                                    "message": "all user cards are returned"
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
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> getCardsByUserId(Authentication authentication, @PathVariable String userId) {
        User principal = (User) authentication.getPrincipal();
        if (principal.getRole() != UserRole.ADMIN && !principal.getId().equals(userId)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        List<CardDto> allCardDtosByUserId = userService
            .getUserById(userId)
            .getCards()
            .stream()
            .map(cardMapper::mapCardToCardDto)
            .toList();
        Response response = Response.getSuccessResponse(allCardDtosByUserId, "user cards are returned");
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{cardId}")
    @Operation(
        description = "Endpoint for updating account name",
        parameters = {
            @Parameter(
                name = "accountId",
                description = "account id to update",
                examples = {
                    @ExampleObject(
                        name = "account id",
                        value = "642s143142f5g24232653b2"
                    )
                },
                in = ParameterIn.PATH
            ),
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
            description = "This is request body for card data update",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateAccountRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Update Card",
                        description = "update Card name request body.",
                        value = """
                            {
                              "name": "A Card"
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Card name successfully updated",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                    {
                                        status": "success",
                                        "value": {
                                            "id": "65b383308520c64e88810063",
                                            "cardName": "A Card",
                                            "cardNumber": "4934393881112354",
                                            "cardType": "VISA",
                                            "expirationDate": "01/29",
                                            "accountNumber": "6830914089245145",
                                            "cardHolderFullName": "CLARK KENT",
                                            "cvv": "942",
                                            "colour" : {
                                                "firstHex": "#02F0BE",
                                                "secondHex": "#05C29B"
                                            },
                                            "currency": "AMD",
                                            "balance": 150000.32
                                          },
                                        "message" : "card updated successfully"
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
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Card Not Found",
                            description = "when card by id not found",
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
                                    "message" : "Invalid credentials - Name can not be blank"
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
    public ResponseEntity<Response> updateCardName(
        @PathVariable String cardId,
        @RequestBody @Valid UpdateNameRequest request,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();

        Optional<String> cardIdOptional = principal
            .getCards()
            .stream()
            .map(Card::getId)
            .filter(streamCardId -> streamCardId.equals(cardId))
            .findAny();

        if (cardIdOptional.isEmpty() && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Card card = cardService.updateCardName(cardId, request.getName());
        CardDto cardDto = cardMapper.mapCardToCardDto(card);

        Response response = Response.getSuccessResponse(cardDto, "card updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    @Operation(
        description = "Deletes the details of a specific Card.",
        parameters = {
            @Parameter(
                name = "cardId",
                description = "card id to delete(chang id, it is random number)",
                examples = {
                    @ExampleObject(
                        name = "card id",
                        value = "642s143142f5g24232653b2"
                    )
                },
                in = ParameterIn.PATH
            ),
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Card successfully returned",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                    {
                                      "status": "success",
                                      "value": null,
                                      "message": "card created"
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
                responseCode = "400",
                description = "Bad Request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Card Not Found",
                            description = "when user by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Card Not Found"
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

    public ResponseEntity<Response> deleteCardById(@PathVariable String cardId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Card card = cardService.getCardById(cardId);
        if (user.getRole() != UserRole.ADMIN && !user.getCards().contains(card)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        cardService.deleteById(cardId);
        Response response = Response.getSuccessResponse("Card deleted successfully");
        return ResponseEntity.ok(response);
    }
}
