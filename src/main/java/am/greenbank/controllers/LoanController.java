package am.greenbank.controllers;

import am.greenbank.dtos.LoanDto;
import am.greenbank.entities.Notification;
import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.exceptions.exceptions.LoanException;
import am.greenbank.helpers.mappers.LoanMapper;
import am.greenbank.helpers.mappers.NotificationMapper;
import am.greenbank.requests.CreateLoanRequest;
import am.greenbank.requests.UpdateLoanOrDepositRequest;
import am.greenbank.responses.Response;
import am.greenbank.services.CreditworthinessService;
import am.greenbank.services.LoanService;
import am.greenbank.services.NotificationServie;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan", description = "Loan Description")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final LoanMapper loanMapper;
    private final UserService userService;
    private final CreditworthinessService creditworthinessService;
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
                schema = @Schema(implementation = CreateLoanRequest.class),
                examples = {
                    @ExampleObject(
                        name = "create loan request",
                        description = "This is request body example for loan creation",
                        value = """
                            {
                                "amount": 70000,
                                "userId": "65b2c58757a3e03f8b922ccb",
                                "currency": "AMD",
                                "to": "1111222233334444",
                                "percent": 0.8,
                                "loanName":"some name",
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
                description = "Success response for create loan request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for create loan request",
                            description = "Success response for create loan request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "stayedAmount": 70000,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "IN_PROGRESS",
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "loanName":"some name",
                                        "duration": 8,
                                        "payment": 700
                                    },
                                    "message" : "Loan is given successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Loan creation error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "User can not get loan",
                            description = "User can not get loan because his/her creditworthiness status is {here will be user creditworthiness status}",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "User can not get loan because his/her creditworthiness status is {here will be user creditworthiness status}"
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
                            name = "Loan type by name not found",
                            description = "when loan by number not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Loan type not found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Loan type option by name not found",
                            description = "when loan type option by number not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "This option for this loan type doesn't exist"
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
    public ResponseEntity<Response> createLoan(
        @RequestBody @Valid CreateLoanRequest createLoanRequest,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        User userById = userService.getUserById(createLoanRequest.getUserId());
        if (!userById.getId().equals(principal.getId()) && principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
        boolean userHasAccount = userById.getAccounts().stream()
            .anyMatch(account -> account.getAccountNumber().equals(createLoanRequest.getTo()));
        if (!userHasAccount) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        boolean canGiveLoanToUser = loanService.canGiveLoanToUser(userById.getId());
        if (!canGiveLoanToUser) {
            throw new LoanException("User can not get loan because his/her creditworthiness status is");
        }

        Loan loan = loanMapper.mapCreateLoanRequestToLoan(createLoanRequest);
        Loan savedLoan = loanService.createLoan(loan, createLoanRequest.getTo());
        LoanDto loanDto = loanMapper.mapLoanToLoanDto(savedLoan);
        Response response = Response.getSuccessResponse(loanDto, "Loan is given successfully");
        Notification notification = notificationMapper
            .getLoanCreationNotification(
                savedLoan.getUserId(),
                savedLoan.getLoanName(),
                savedLoan.getAmount(),
                savedLoan.getCurrency(),
                createLoanRequest.getTo()
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
                description = "userId for returning users loans",
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
                description = "Success response for getting users loans",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting users loans",
                            description = "Success response for getting users loans",
                            value = """
                                {
                                    "status" : "success",
                                    "value" : [
                                        {
                                            "id": "65b2c58757a3e03f8b933cfb",
                                            "userId": "65b2c58757a3e03f8b922ccb",
                                            "amount": 70000,
                                            "stayedAmount": 70000,
                                            "startDate": ,
                                            "endDate": ,
                                            "status": "IN_PROGRESS",
                                            "currency": "AMD",
                                            "percent": 0.8,
                                            "loanName":"some name",
                                            "duration": 8,
                                            "payment": 700
                                        }
                                    ],
                                    "message" : "User loans are returned successfully"
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
    public ResponseEntity<Response> getLoansByUserId(@PathVariable String userId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        User userById = userService.getUserById(userId);
        if (!userById.getId().equals(principal.getId()) && principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        List<Loan> loans = loanService.getLoansByUserId(userId);
        List<LoanDto> loanDtos = loans
            .stream()
            .map(loanMapper::mapLoanToLoanDto)
            .toList();
        Response response = Response.getSuccessResponse(loanDtos, "User loans are returned successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{loanId}")
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
                name = "loanId",
                description = "loanId for paying loan with that id",
                examples = {
                    @ExampleObject(
                        name = "loanId",
                        value = "65b2c58757a3e03f8b933cfb"
                    ),
                },
                in = ParameterIn.PATH
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
                        name = "create loan request",
                        description = "This is request body example for paying loan",
                        value = """
                            {
                                "amount": 700,
                                "from": "5555666677778888"
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for pay loan request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for pay loan request",
                            description = "Success response for pay loan request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "stayedAmount": 69300,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "IN_PROGRESS",
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "loanName":"some name",
                                        "duration": 8,
                                        "payment": 700
                                    },
                                    "message" : "Loan Payed successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Loan creation error response",
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
                            name = "Loan not found",
                            description = "when loan by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Loan Not Found"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Loan is paid",
                            description = "when loan is already paid",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Loan is already paid"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "Loan payment minimal amount error",
                            description = "when loan minimal payment amount is not payed",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Minimal amount for this loan payment is {minimal Payment Amount}"
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
    public ResponseEntity<Response> payLoan(
        @PathVariable String loanId,
        @RequestBody @Valid UpdateLoanOrDepositRequest updateLoanOrDepositRequest,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        if (principal.getRole() == UserRole.USER) {
            boolean userHasAccount = principal
                .getAccounts()
                .stream()
                .anyMatch(account -> account.getAccountNumber().equals(updateLoanOrDepositRequest.getFrom()));
            if (!userHasAccount) {
                throw new AccessDeniedException("User can access to data connected to him");
            }
            boolean userHasLoan = loanService
                .getLoansByUserId(principal.getId())
                .stream()
                .anyMatch(loan -> loan.getId().equals(loanId));
            if (!userHasLoan) {
                throw new AccessDeniedException("User can access to data connected to him");
            }
        } else if (principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Double amountBeforePaying = loanService.getLoan(loanId).getStayedAmount();
        Loan loan = loanService.payLoan(loanId, updateLoanOrDepositRequest.getAmount(), updateLoanOrDepositRequest.getFrom());
        LoanDto loanDto = loanMapper.mapLoanToLoanDto(loan);
        Response response = Response.getSuccessResponse(loanDto, "Loan Payed successfully");
        Notification notification = notificationMapper.getNotification(
            "Loan",
            loan.getUserId(),
            loan.getLoanName(),
            "payed",
            amountBeforePaying - loan.getStayedAmount(),
            loan.getCurrency(),
            updateLoanOrDepositRequest.getFrom()
        );
        notificationServie.sendNotification(notification);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanId}")
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
                name = "loanId",
                description = "loanId for paying loan with that id",
                examples = {
                    @ExampleObject(
                        name = "loanId",
                        value = "65b2c58757a3e03f8b933cfb"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for pay loan request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for pay loan request",
                            description = "Success response for pay loan request",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b933cfb",
                                        "userId": "65b2c58757a3e03f8b922ccb",
                                        "amount": 70000,
                                        "stayedAmount": 69300,
                                        "startDate": ,
                                        "endDate": ,
                                        "status": "IN_PROGRESS",
                                        "currency": "AMD",
                                        "percent": 0.8,
                                        "loanName":"some name",
                                        "duration": 8,
                                        "payment": 700
                                    },
                                    "message" : "Loan returned successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Loan creation error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Loan not found",
                            description = "when loan by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Loan Not Found"
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
    public ResponseEntity<Response> getLoan(
        @PathVariable String loanId,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();
        if (principal.getRole() == UserRole.USER) {
            boolean userHasLoan = loanService
                .getLoansByUserId(principal.getId())
                .stream()
                .anyMatch(loan -> loan.getId().equals(loanId));
            if (!userHasLoan) {
                throw new AccessDeniedException("User can access to data connected to him");
            }
        } else if (principal.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Loan loan = loanService.getLoan(loanId);
        LoanDto loanDto = loanMapper.mapLoanToLoanDto(loan);
        Response response = Response.getSuccessResponse(loanDto, "Loan returned successfully");
        return ResponseEntity.ok(response);
    }
}