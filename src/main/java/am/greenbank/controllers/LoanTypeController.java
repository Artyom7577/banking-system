package am.greenbank.controllers;

import am.greenbank.dtos.LoanTypeDto;
import am.greenbank.entities.loan.LoanType;
import am.greenbank.entities.Option;
import am.greenbank.helpers.mappers.LoanTypeMapper;
import am.greenbank.requests.CreateLoanTypeRequest;
import am.greenbank.requests.UpdateLoanTypeRequest;
import am.greenbank.responses.Response;
import am.greenbank.services.LoanTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loanType")
@Tag(name = "Loan Type", description = "Loan Type Description")
@RequiredArgsConstructor
public class LoanTypeController {
    private final LoanTypeService loanTypeService;
    private final LoanTypeMapper loanTypeMapper;

    @PostMapping("")
    @Operation(
        description = "This is admin endpoint for adding loan type",
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
            description = "This is request bodies for creating loan type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateLoanTypeRequest.class),
                examples = {
                    @ExampleObject(
                        name = "add new loan type request",
                        description = "This is request body example for add loan type request",
                        value = """
                            {
                                "name": "some loan",
                                "options": [
                                    {
                                        "duration": 4,
                                        "percent": 0.8
                                    }
                                ]
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Success response for add loan type request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for add loan type request",
                            description = "Success response for add loan type request",
                            value = """
                                {
                                    "status" : "error",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some loan",
                                        "options": [
                                            {
                                                "duration": 4,
                                                "percent": 0.8
                                            }
                                        ],
                                        "available": true
                                    },
                                    "message" : "Loan type added"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Loan type with this name already exists",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type with this name already exists"
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
                            description = "when user without admin role tries to access this endpoint",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Access Denied"
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createLoanType(@RequestBody CreateLoanTypeRequest createLoanTypeRequest) {
        LoanType loanType = loanTypeMapper.mapCreateLoanTypeRequestToLoan(createLoanTypeRequest);
        LoanType savedLoanType = loanTypeService.createLoanType(loanType);
        LoanTypeDto loanTypeDto = loanTypeMapper.mapLoanTypeToLoanTypeDto(savedLoanType);
        Response response = Response.getSuccessResponse(loanTypeDto, "Loan type added");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("")
    @Operation(
        description = "This is admin endpoint for getting all loan type states",
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for getting all loan type states request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting all loan type states request",
                            description = "Success response for getting all loan type states request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some loan",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                }
                                            ],
                                            "available": true
                                        }
                                    ],
                                    "message": "All loan types are returned successfully"
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
                            description = "when user without admin role tries to access this endpoint",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Access Denied"
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getAllLoanTypes() {
        List<LoanType> loanTypes = loanTypeService.getAll();
        List<LoanTypeDto> loanTypeDtos = loanTypes
            .stream()
            .map(loanTypeMapper::mapLoanTypeToLoanTypeDto)
            .toList();
        Response response = Response.getSuccessResponse(loanTypeDtos, "All loan types are returned successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{loanName}/addOption")
    @Operation(
        description = "This is admin endpoint for getting all loan type states",
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
                name = "loanName",
                description = "Loan type name",
                examples = {
                    @ExampleObject(
                        name = "Loan Name",
                        value = "some loan"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for adding option to loan type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Option.class),
                examples = {
                    @ExampleObject(
                        name = "add new loan type option request",
                        description = "This is request body example for add loan type option request",
                        value = """
                            {
                                "duration": 8,
                                "percent": 0.6
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for add option to loan type request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for add option to loan type request",
                            description = "Success response for add option to loan type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some loan",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                },
                                                {
                                                    "duration": 8,
                                                    "percent": 0.6
                                                }
                                            ],
                                            "available": true
                                        }
                                    ],
                                    "message": "Loan type option added"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad requests for loan type add option",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Loan type with provided name not found",
                            description = "Loan type not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Loan type option already exists",
                            description = "Loan type option already exists",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type option already exists"
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
                            description = "when user without admin role tries to access this endpoint",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Access Denied"
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> addOptionToLoanTypeByName(
        @PathVariable String loanName,
        @RequestBody Option option
    ) {
        LoanType loanType = loanTypeService.addOptionToLoanType(loanName, option);
        LoanTypeDto loanTypeDto = loanTypeMapper.mapLoanTypeToLoanTypeDto(loanType);
        Response response = Response.getSuccessResponse(loanTypeDto, "Loan type option added");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{loanName}/removeOption")
    @Operation(
        description = "This is admin endpoint for getting all loan type states",
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
                name = "loanName",
                description = "Loan type name",
                examples = {
                    @ExampleObject(
                        name = "Loan Name",
                        value = "some loan"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for removing option from loan type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Option.class),
                examples = {
                    @ExampleObject(
                        name = "remove loan type option request",
                        description = "This is request body example for remove loan type option request",
                        value = """
                            {
                                "duration": 8,
                                "percent": 0.6
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for remove option from loan type request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for remove option from loan type request",
                            description = "Success response for remove option from loan type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some loan",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                },
                                            ],
                                            "available": true
                                        }
                                    ],
                                    "message": "Loan type option removed"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad requests for loan type add option",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Loan type not found",
                            description = "Loan type with provided name not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Option for loan type not found",
                            description = "This option for this loan type doesn't exist",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "This option for this loan type doesn't exist"
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
                            description = "when user without admin role tries to access this endpoint",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Access Denied"
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> removeOptionToLoanTypeByName(
        @PathVariable String loanName,
        @RequestBody Option option
    ) {
        LoanType loanType = loanTypeService.removeOptionToLoanType(loanName, option);
        LoanTypeDto loanTypeDto = loanTypeMapper.mapLoanTypeToLoanTypeDto(loanType);
        Response response = Response.getSuccessResponse(loanTypeDto, "Loan type option removed");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{loanName}")
    @Operation(
        description = "This is admin endpoint for getting all loan type states",
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
                name = "loanName",
                description = "Loan type name",
                examples = {
                    @ExampleObject(
                        name = "Loan Name",
                        value = "some loan"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for updating loan type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateLoanTypeRequest.class),
                examples = {
                    @ExampleObject(
                        name = "update loan type request",
                        description = "This is request body example for update loan type request",
                        value = """
                            {
                                "name": "Other loan",
                                "available": false
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for update loan type",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for update loan type request",
                            description = "Success response for update loan type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "Other loan",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                },
                                            ],
                                            "available": false
                                        }
                                    ],
                                    "message": "Loan type updated successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad requests for loan type add option",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Loan type not found",
                            description = "Loan type with provided name not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Loan type with name exists",
                            description = "Loan type with this name already exists",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type with this name already exists"
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
                            description = "when user without admin role tries to access this endpoint",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Access Denied"
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateLoanTypeByName(
        @PathVariable String loanName,
        @RequestBody UpdateLoanTypeRequest updateLoanTypeRequest
    ) {
        LoanType loanType = loanTypeService.updateLoanType(
            loanName, updateLoanTypeRequest.getName(), updateLoanTypeRequest.getAvailable()
        );
        LoanTypeDto loanTypeDto = loanTypeMapper.mapLoanTypeToLoanTypeDto(loanType);
        Response response = Response.getSuccessResponse(loanTypeDto, "Loan type updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{loanName}")
    @Operation(
        description = "This is admin endpoint for deleting loan type",
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
                name = "loanName",
                description = "Loan type name",
                examples = {
                    @ExampleObject(
                        name = "Loan Name",
                        value = "some loan"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for update loan type",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for update loan type request",
                            description = "Success response for update loan type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : null,
                                    "message": "Loan type deleted successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Loan type deletion error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Loan type with name provided name not found",
                            description = "Loan type with name provided name not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Loan type is in use",
                            description = "Loan type is in use",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Loan type is in use"
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
                            description = "when user without admin role tries to access this endpoint",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Access Denied"
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteLoanTypeByName(@PathVariable String loanName) {
        loanTypeService.deleteByName(loanName);
        Response response = Response.getSuccessResponse("Loan type deleted successfully");
        return ResponseEntity.ok(response);
    }
}
