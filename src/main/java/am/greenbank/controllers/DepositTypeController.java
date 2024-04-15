package am.greenbank.controllers;

import am.greenbank.dtos.DepositTypeDto;
import am.greenbank.entities.Option;
import am.greenbank.entities.deposit.DepositType;
import am.greenbank.helpers.mappers.DepositTypeMapper;
import am.greenbank.requests.CreateDepositTypeRequest;
import am.greenbank.requests.UpdateDepositTypeRequest;
import am.greenbank.responses.Response;
import am.greenbank.services.DepositTypeService;
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
@RequestMapping("/api/depositType")
@Tag(name = "Deposit Type", description = "Deposit Type Description")
@RequiredArgsConstructor
public class DepositTypeController {
    private final DepositTypeService depositTypeService;
    private final DepositTypeMapper depositTypeMapper;

    @PostMapping("")
    @Operation(
        description = "This is admin endpoint for adding deposit type",
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
            description = "This is request bodies for creating deposit type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateDepositTypeRequest.class),
                examples = {
                    @ExampleObject(
                        name = "add new deposit type request",
                        description = "This is request body example for add deposit type request",
                        value = """
                            {
                                "name": "some deposit",
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
                description = "Success response for add deposit type request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for add deposit type request",
                            description = "Success response for add deposit type request",
                            value = """
                                {
                                    "status" : "error",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some deposit",
                                        "options": [
                                            {
                                                "duration": 4,
                                                "percent": 0.8
                                            }
                                        ],
                                        "available": true
                                    },
                                    "message" : "Deposit type added"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Deposit type with this name already exists",
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
                                    "message" : "Deposit type with this name already exists"
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
    public ResponseEntity<Response> createDepositType(@RequestBody CreateDepositTypeRequest createDepositTypeRequest) {
        DepositType depositType = depositTypeMapper.mapCreateDepositTypeRequestToDeposit(createDepositTypeRequest);
        DepositType savedDepositType = depositTypeService.createDepositType(depositType);
        DepositTypeDto depositTypeDto = depositTypeMapper.mapDepositTypeToDepositTypeDto(savedDepositType);
        Response response = Response.getSuccessResponse(depositTypeDto, "Deposit type added");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("")
    @Operation(
        description = "This is admin endpoint for getting all deposit type states",
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
                description = "Success response for getting all deposit type states request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting all deposit type states request",
                            description = "Success response for getting all deposit type states request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some deposit",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                }
                                            ],
                                            "available": true
                                        }
                                    ],
                                    "message": "All deposit types are returned successfully"
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
    public ResponseEntity<Response> getAllDepositTypes() {
        List<DepositType> depositTypes = depositTypeService.getAll();
        List<DepositTypeDto> depositTypeDtos = depositTypes
            .stream()
            .map(depositTypeMapper::mapDepositTypeToDepositTypeDto)
            .toList();
        Response response = Response.getSuccessResponse(depositTypeDtos, "All deposit types are returned successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{depositName}/addOption")
    @Operation(
        description = "This is admin endpoint for getting all deposit type states",
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
                name = "depositName",
                description = "Deposit type name",
                examples = {
                    @ExampleObject(
                        name = "Deposit Name",
                        value = "some deposit"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for adding option to deposit type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Option.class),
                examples = {
                    @ExampleObject(
                        name = "add new deposit type option request",
                        description = "This is request body example for add deposit type option request",
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
                description = "Success response for add option to deposit type request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for add option to deposit type request",
                            description = "Success response for add option to deposit type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some deposit",
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
                                    "message": "Deposit type option added"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad requests for deposit type add option",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Deposit type with provided name not found",
                            description = "Deposit type not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Deposit type option already exists",
                            description = "Deposit type option already exists",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type option already exists"
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
    public ResponseEntity<Response> addOptionToDepositTypeByName(
        @PathVariable String depositName,
        @RequestBody Option option
    ) {
        DepositType depositType = depositTypeService.addOptionToDepositType(depositName, option);
        DepositTypeDto depositTypeDto = depositTypeMapper.mapDepositTypeToDepositTypeDto(depositType);
        Response response = Response.getSuccessResponse(depositTypeDto, "Deposit type option added");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{depositName}/removeOption")
    @Operation(
        description = "This is admin endpoint for getting all deposit type states",
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
                name = "depositName",
                description = "Deposit type name",
                examples = {
                    @ExampleObject(
                        name = "Deposit Name",
                        value = "some deposit"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for removing option from deposit type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Option.class),
                examples = {
                    @ExampleObject(
                        name = "remove deposit type option request",
                        description = "This is request body example for remove deposit type option request",
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
                description = "Success response for remove option from deposit type request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for remove option from deposit type request",
                            description = "Success response for remove option from deposit type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "some deposit",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                },
                                            ],
                                            "available": true
                                        }
                                    ],
                                    "message": "Deposit type option removed"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad requests for deposit type add option",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Deposit type not found",
                            description = "Deposit type with provided name not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Option for deposit type not found",
                            description = "This option for this deposit type doesn't exist",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "This option for this deposit type doesn't exist"
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
    public ResponseEntity<Response> removeOptionToDepositTypeByName(
        @PathVariable String depositName,
        @RequestBody Option option
    ) {
        DepositType depositType = depositTypeService.removeOptionToDepositType(depositName, option);
        DepositTypeDto depositTypeDto = depositTypeMapper.mapDepositTypeToDepositTypeDto(depositType);
        Response response = Response.getSuccessResponse(depositTypeDto, "Deposit type option removed");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{depositName}")
    @Operation(
        description = "This is admin endpoint for getting all deposit type states",
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
                name = "depositName",
                description = "Deposit type name",
                examples = {
                    @ExampleObject(
                        name = "Deposit Name",
                        value = "some deposit"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "This is request bodies for updating deposit type",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateDepositTypeRequest.class),
                examples = {
                    @ExampleObject(
                        name = "update deposit type request",
                        description = "This is request body example for update deposit type request",
                        value = """
                            {
                                "name": "Other deposit",
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
                description = "Success response for update deposit type",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for update deposit type request",
                            description = "Success response for update deposit type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "name": "Other deposit",
                                        "options": [
                                                {
                                                    "duration": 4,
                                                    "percent": 0.8
                                                },
                                            ],
                                            "available": false
                                        }
                                    ],
                                    "message": "Deposit type updated successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad requests for deposit type add option",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Deposit type not found",
                            description = "Deposit type with provided name not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Deposit type with name exists",
                            description = "Deposit type with this name already exists",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type with this name already exists"
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
    public ResponseEntity<Response> updateDepositTypeByName(
        @PathVariable String depositName,
        @RequestBody UpdateDepositTypeRequest updateDepositTypeRequest
    ) {
        DepositType depositType = depositTypeService.updateDepositType(
            depositName, updateDepositTypeRequest.getName(), updateDepositTypeRequest.getAvailable()
        );
        DepositTypeDto depositTypeDto = depositTypeMapper.mapDepositTypeToDepositTypeDto(depositType);
        Response response = Response.getSuccessResponse(depositTypeDto, "Deposit type updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{depositName}")
    @Operation(
        description = "This is admin endpoint for deleting deposit type",
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
                name = "depositName",
                description = "Deposit type name",
                examples = {
                    @ExampleObject(
                        name = "Deposit Name",
                        value = "some deposit"

                    ),
                },
                in = ParameterIn.HEADER
            ),
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for update deposit type",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for update deposit type request",
                            description = "Success response for update deposit type request",
                            value = """
                                {
                                    "status": "success",
                                    "value" : null,
                                    "message": "Deposit type deleted successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Deposit type deletion error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Deposit type with name provided name not found",
                            description = "Deposit type with name provided name not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type with name provided name not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Deposit type is in use",
                            description = "Deposit type is in use",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Deposit type is in use"
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
    public ResponseEntity<Response> deleteDepositTypeByName(@PathVariable String depositName) {
        depositTypeService.deleteByName(depositName);
        Response response = Response.getSuccessResponse("Deposit type deleted successfully");
        return ResponseEntity.ok(response);
    }
}
