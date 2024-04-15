package am.greenbank.controllers;

import am.greenbank.dtos.AccountDto;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.exceptions.exceptions.AccountNotFoundException;
import am.greenbank.helpers.mappers.AccountMapper;
import am.greenbank.requests.CreateAccountRequest;
import am.greenbank.requests.UpdateNameRequest;
import am.greenbank.responses.Response;
import am.greenbank.services.AccountService;
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
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account Description")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;
    private final AccountMapper accountMapper;

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
                description = "Accounts Successfully returned",
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
                                        "id": "65b2c58646a3e03f8b922bb9",
                                        "accountName": "Default name",
                                        "accountNumber": "6280928645705247",
                                        "balance": 7441.621043306813,
                                        "currency": "AMD",
                                        "accountType": "CURRENT",
                                        "isDefault": true
                                      }
                                    ],
                                    "message": "all user accounts are returned"
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
    public ResponseEntity<Response> getAccounts(Authentication authentication, @PathVariable String userId) {
        User principal = (User) authentication.getPrincipal();
        if (principal.getRole() != UserRole.ADMIN && !principal.getId().equals(userId)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        List<Account> accounts = userService.getUserById(userId).getAccounts();
        List<AccountDto> allCardDtosByUserId = accounts.stream().map(accountMapper::mapAccountToAccountDto).toList();
        Response response = Response.getSuccessResponse(allCardDtosByUserId, "all user accounts are returned");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/")
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
                schema = @Schema(implementation = CreateAccountRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Create Account",
                        description = "create account request body. (Change id it is random)",
                        value = """
                            {
                              "ownerId": "6fhjifg25fg27tw135142v453",
                              "accountType": "CURRENT",
                              "currency": "AMD"
                            }
                            """
                    )
                }
            )
        ),

        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Successfully account created",
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
                                            "id": "65b2c58646a3e03f8b922bb9",
                                            "accountName": "",
                                            "accountNumber": "6280928645705247",
                                            "balance": 7441.621043306813,
                                            "currency": "AMD",
                                            "accountType": "CURRENT",
                                            "isDefault": false
                                          },
                                        "message" : "Account is created"
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
                            name = "Validation error",
                            description = "When one or more field of request body doesn't pass validation",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Invalid credentials - Owner ID must not be blank\\AccountType type must not be null\\nCurrency must not be null"
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
    public ResponseEntity<Response> createAccount(@RequestBody @Valid CreateAccountRequest accountRequest, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(accountRequest.getOwnerId()) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Account account = accountService.createAccount(
            accountRequest.getAccountType(),
            accountRequest.getOwnerId(),
            accountRequest.getCurrency()
        );
        AccountDto accountDto = accountMapper.mapAccountToAccountDto(account);
        Response response = Response.getSuccessResponse(accountDto, "Account is created");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}")
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
            description = "This is request body for user data update",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateAccountRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Update Account",
                        description = "update account request body.",
                        value = """
                            {
                              "name": "An account"
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Account name successfully updated",
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
                                            "id": "642s143142f5g24232653b2",
                                            "accountName": "An account",
                                            "accountNumber": "6280928645705247",
                                            "balance": 7441.621043306813,
                                            "currency": "AMD",
                                            "accountType": "CURRENT",
                                            "isDefault": false
                                          },
                                        "message" : "account is updated"
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
                            name = "Account Not Found",
                            description = "when user by id not found",
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
    public ResponseEntity<Response> updateAccountName(@PathVariable String accountId, @RequestBody @Valid UpdateNameRequest request, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();

        Optional<String> accountIdOptional = principal
            .getAccounts()
            .stream()
            .map(Account::getId)
            .filter(streamAccountId -> streamAccountId.equals(accountId))
            .findAny();

        if (accountIdOptional.isEmpty() && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Account account = accountService.updateAccountName(accountId, request.getName());
        AccountDto accountDto = accountMapper.mapAccountToAccountDto(account);

        Response response = Response.getSuccessResponse(accountDto, "account is updated");

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/total-balances-by-currency")
//    @Operation(
//        description = "Get total balances by currency.",
//        parameters = {
//            @Parameter(
//                name = "X-platform",
//                description = "Platform name either \"ios\" or \"web\"",
//                examples = {
//                    @ExampleObject(
//                        name = "Web browser",
//                        value = "web"
//
//                    ),
//                    @ExampleObject(
//                        name = "Apple smartphone",
//                        value = "ios"
//                    )
//                },
//                in = ParameterIn.HEADER
//            )
//        },
//        responses = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved total balances by currency")
//
//        },
//        security = {
//            @SecurityRequirement(
//                name = "bearerAuth"
//            )
//        }
//    )
//
//    public ResponseEntity<Response> getTotalBalancesByCurrency() {
//        Map<String, Double> totalBalancesByCurrency = accountService.getTotalBalancesByCurrency();
//
//        Response response = Response.getSuccessResponse(totalBalancesByCurrency, "all balances by their currency is returned");
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/{accountId}")
    @Operation(
        description = "Fetches the details of a specific account.",
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Account successfully returned",
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
                                            "id": "642s143142f5g24232653b2",
                                            "accountName": "An account",
                                            "accountNumber": "6280928645705247",
                                            "balance": 7441.621043306813,
                                            "currency": "AMD",
                                            "accountType": "CURRENT",
                                            "isDefault": false
                                          },
                                        "message" : "account is returned"
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
                            name = "Account Not Found",
                            description = "when user by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Account Not Found"
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
    public ResponseEntity<Response> getAccountById(@PathVariable String accountId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Account account = accountService.getAccountById(accountId);
        if (user.getRole() != UserRole.ADMIN && !user.getAccounts().contains(account)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        AccountDto accountDto = accountMapper.mapAccountToAccountDto(account);
        Response response = Response.getSuccessResponse(accountDto, "account is returned");
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{accountId}")
    @Operation(
        description = "deletes the details of a specific account.",
        parameters = {
            @Parameter(
                name = "accountId",
                description = "account id to delete",
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Account successfully deleted",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                    {
                                        status": "success",
                                        "value": null,
                                        "message" : "Account deleted successfully"
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
                            description = "when user whom account by id belongs not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "User Not Found"
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
    public ResponseEntity<Response> deleteAccountById(@PathVariable String accountId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Account account = accountService.getAccountById(accountId);
        if (user.getRole() != UserRole.ADMIN && !user.getAccounts().contains(account)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        accountService.deleteById(accountId);
        Response response = Response.getSuccessResponse("Account deleted successfully");
        return ResponseEntity.ok(response);
    }
}
