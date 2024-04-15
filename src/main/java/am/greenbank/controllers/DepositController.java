package am.greenbank.controllers;

import am.greenbank.dtos.DepositDto;
import am.greenbank.entities.Notification;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.exceptions.exceptions.UnsupportedValueException;
import am.greenbank.helpers.mappers.DepositMapper;
import am.greenbank.helpers.mappers.NotificationMapper;
import am.greenbank.requests.CreateDepositRequest;
import am.greenbank.requests.UpdateLoanOrDepositRequest;
import am.greenbank.responses.Response;
import am.greenbank.services.DepositService;
import am.greenbank.services.NotificationServie;
import am.greenbank.services.UserService;
import am.greenbank.entities.deposit.Deposit;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deposits")
@Tag(name = "Deposit", description = "Deposit Description")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;
    private final DepositMapper depositMapper;
    private final UserService userService;
    private final NotificationServie notificationServie;
    private final NotificationMapper notificationMapper;

    @PostMapping("")
    @Operation(
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
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for template creation",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateDepositRequest.class),
                examples = {
                    @ExampleObject(
                        name = "create deposit request",
                        description = "This is request body example for deposit creation",
                        value = """
                            {
                                "amount": 70000,
                                "userId": "65b2c58757a3e03f8b922ccb",
                                "currency": "AMD",
                                "from": "1111222233334444",
                                "percent": 0.8,
                                "depositName":"some name",
                                "duration": 8
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Success response for create deposit request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for create deposit request",
                            description = "Success response for create deposit request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "IN_PROGRESS",
                                        "from": "1111222233334444"
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "depositName":"some name",
                                        "duration": 8
                                    },
                                    "message" : "Deposit is created successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Deposit creation error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "User can not get deposit",
                            description = "User can not get deposit because his/her creditworthiness status is {here will be user creditworthiness status}",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "User can not get deposit because his/her creditworthiness status is {here will be user creditworthiness status}"
                                }
                                """
                        ),
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
                            name = "Deposit type by name not found",
                            description = "when deposit by number not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Deposit type not found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Deposit type option by name not found",
                            description = "when deposit type option by number not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "This option for this deposit type doesn't exist"
                                    }
                                """
                        ),
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
    public ResponseEntity<Response> createDeposit(
        @RequestBody @Valid CreateDepositRequest createDepositRequest,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        User userById = userService.getUserById(createDepositRequest.getUserId());
        if (!userById.getId().equals(principal.getId()) && principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
        boolean userHasAccount = userById.getAccounts().stream()
            .anyMatch(account -> account.getAccountNumber().equals(createDepositRequest.getFrom()));
        if (!userHasAccount) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Deposit deposit = depositMapper.mapCreateDepositRequestToDeposit(createDepositRequest);
        Deposit savedDeposit = depositService.createDeposit(deposit);
        DepositDto depositDto = depositMapper.mapDepositToDepositDto(savedDeposit);
        Response response = Response.getSuccessResponse(depositDto, "Deposit is created successfully");
        Notification notification = notificationMapper
            .getDepositCreationNotification(
                deposit.getUserId(),
                deposit.getDepositName(),
                deposit.getAmount(),
                deposit.getCurrency(),
                deposit.getFrom()
            );

        notificationServie.sendNotification(notification);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/{userId}")
    @Operation(
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
                name = "userId",
                description = "userId for returning users deposits",
                examples = {
                    @ExampleObject(
                        name = "userId",
                        value = "65b2c58646a3e03f8b922bba"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for getting users deposits",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting users deposits",
                            description = "Success response for getting users deposits",
                            value = """
                                {
                                    "status" : "success",
                                    "value" : [
                                        {
                                            "id": "65b2c58757a3e03f8b933cfb",
                                            "userId": "65b2c58757a3e03f8b922ccb",
                                            "amount": 70000,
                                            "startDate": ,
                                            "endDate": ,
                                            "status": "IN_PROGRESS",
                                            "from": "1111222233334444"
                                            "currency": "AMD",
                                            "percent": 0.8,
                                            "depositName":"some name",
                                            "duration": 8
                                        }
                                    ],
                                    "message" : "User deposits are returned successfully"
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
    public ResponseEntity<Response> getDepositsByUserId(@PathVariable String userId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        User userById = userService.getUserById(userId);
        if (!userById.getId().equals(principal.getId()) && principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        List<Deposit> deposits = depositService.getDepositsByUserId(userId);
        List<DepositDto> depositDtos = deposits
            .stream()
            .map(depositMapper::mapDepositToDepositDto)
            .toList();
        Response response = Response.getSuccessResponse(depositDtos, "User deposits are returned successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{depositId}")
    @Operation(
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
                name = "depositId",
                description = "depositId for paying deposit with that id",
                examples = {
                    @ExampleObject(
                        name = "depositId",
                        value = "65b2c58757a3e03f8b933cfb"
                    ),
                },
                in = ParameterIn.PATH
            ),
            @Parameter(
                name = "updateType",
                description = "updateType for closing deposit or updating amount of deposit by adding to it",
                examples = {
                    @ExampleObject(
                        name = "updateType",
                        value = "addAmount"
                    ),
                    @ExampleObject(
                        name = "updateType",
                        value = "takeAll"
                    ),
                },
                in = ParameterIn.QUERY
            )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for template creation",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateLoanOrDepositRequest.class),
                examples = {
                    @ExampleObject(
                        name = "create deposit request",
                        description = "This is request body example for paying deposit",
                        value = """
                            {
                                "amount": 700,
                                "from": "5555666677778888",
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for pay deposit request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for pay deposit request",
                            description = "Success response for pay deposit request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "IN_PROGRESS",
                                        "from": "1111222233334444"
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "depositName":"some name",
                                        "duration": 8
                                    },
                                    "message" : "Deposit updated successfully"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Success response for pay deposit request",
                            description = "Success response for pay deposit request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "CLOSED",
                                        "from": "1111222233334444"
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "depositName":"some name",
                                        "duration": 8
                                    },
                                    "message" : "Deposit closed successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Deposit creation error response",
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
                            name = "Deposit not found",
                            description = "when deposit by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Deposit Not Found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Deposit is paid",
                            description = "when deposit is already paid",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Deposit is already paid"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Deposit payment minimal amount error",
                            description = "when deposit minimal payment amount is not payed",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Minimal amount for this deposit payment is {minimal Payment Amount}"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Insufficient funds",
                            description = "when amount in account is not enough for paying",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Insufficient funds"
                                    }
                                """
                        ),

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
    public ResponseEntity<Response> updateDeposit(
        @PathVariable String depositId,
        @RequestParam(name = "updateType") String updateType,
        @RequestBody(required = false) @Valid UpdateLoanOrDepositRequest updateDepositRequest,
        Authentication authentication
    ) {
        validateUpdateType(updateType);

        User principal = (User) authentication.getPrincipal();
        if (principal.getRole() == UserRole.USER) {
            boolean userHasAccount = principal
                .getAccounts()
                .stream()
                .anyMatch(account -> account.getAccountNumber().equals(updateDepositRequest.getFrom()));
            if (!userHasAccount) {
                throw new AccessDeniedException("User can access to data connected to him");
            }
            boolean userHasDeposit = depositService
                .getDepositsByUserId(principal.getId())
                .stream()
                .anyMatch(deposit -> deposit.getId().equals(depositId));
            if (!userHasDeposit) {
                throw new AccessDeniedException("User can access to data connected to him");
            }
        } else if (principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Response response;
        Notification notification = null;
        if (updateType.equals("addAmount")) {
            Deposit deposit = depositService.updateDeposit(depositId, updateDepositRequest.getAmount(), updateDepositRequest.getFrom());
            DepositDto depositDto = depositMapper.mapDepositToDepositDto(deposit);
            response = Response.getSuccessResponse(depositDto, "Deposit updated successfully");
            notification = notificationMapper
                .getNotification(
                    "Deposit",
                    deposit.getUserId(),
                    deposit.getDepositName(),
                    "updated",
                    updateDepositRequest.getAmount(),
                    deposit.getCurrency(),
                    updateDepositRequest.getFrom()
                );
        } else {
            Deposit deposit = depositService.closeDeposit(depositId);
            DepositDto depositDto = depositMapper.mapDepositToDepositDto(deposit);
            response = Response.getSuccessResponse(depositDto, "Deposit closed successfully");
            notification = notificationMapper
                .getDepositClosingNotification(
                    deposit.getUserId(),
                    deposit.getDepositName(),
                    updateDepositRequest.getAmount(),
                    deposit.getCurrency(),
                    updateDepositRequest.getFrom()
                );
        }

        if (notification != null) {
            notificationServie.sendNotification(notification);
        }

        return ResponseEntity.ok(response);
    }

    private void validateUpdateType(String updateType) {
        if (!updateType.equals("addAmount") && !updateType.equals("takeAll")) {
            throw new UnsupportedValueException("updateType query param doesn't support the value you send");
        }
    }

    @GetMapping("/{depositId}")
    @Operation(
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
                name = "depositId",
                description = "depositId for paying deposit with that id",
                examples = {
                    @ExampleObject(
                        name = "depositId",
                        value = "65b2c58757a3e03f8b933cfb"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for pay deposit request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for pay deposit request",
                            description = "Success response for pay deposit request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "IN_PROGRESS",
                                        "from": "1111222233334444"
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "depositName":"some name",
                                        "duration": 8
                                    },
                                    "message" : "Deposit returned successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Deposit creation error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Deposit not found",
                            description = "when deposit by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Deposit Not Found"
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
    public ResponseEntity<Response> getDeposit(
        @PathVariable String depositId,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        if (principal.getRole() == UserRole.USER) {
            boolean userHasDeposit = depositService
                .getDepositsByUserId(principal.getId())
                .stream()
                .anyMatch(deposit -> deposit.getId().equals(depositId));
            if (!userHasDeposit) {
                throw new AccessDeniedException("User can access to data connected to him");
            }
        } else if (principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Deposit deposit = depositService.getDeposit(depositId);
        DepositDto depositDto = depositMapper.mapDepositToDepositDto(deposit);
        Response response = Response.getSuccessResponse(depositDto, "Deposit returned successfully");
        return ResponseEntity.ok(response);
    }
}
