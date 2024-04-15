package am.greenbank.controllers;

import am.greenbank.dtos.CreditworthinessDto;
import am.greenbank.dtos.TemplateDto;
import am.greenbank.entities.Creditworthiness;
import am.greenbank.helpers.mappers.CreditworthinessMapper;
import am.greenbank.responses.Response;
import am.greenbank.services.CreditworthinessService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creditworthiness")
@Tag(name = "Creditworthiness", description = "Creditworthiness Description")
@RequiredArgsConstructor
public class CreditworthinessController {
    private final CreditworthinessService creditworthinessService;
    private final CreditworthinessMapper creditworthinessMapper;

    @PostMapping("")
    @Operation(
        description = "This is admin endpoint for adding creditworthiness state",
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
                schema = @Schema(implementation = CreditworthinessDto.class),
                examples = {
                    @ExampleObject(
                        name = "add new creditworthiness state request",
                        description = "This is request body example for add creditworthiness state request",
                        value = """
                            {
                                "order": 2,
                                "unblockDuration": 16,
                                "name": "missed loan payment once",
                                "canGetLoan": true
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for add creditworthiness state request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for add creditworthiness state request",
                            description = "Success response for add creditworthiness state request",
                            value = """
                                {
                                    "status" : "error",
                                    "value" : {
                                        "id": "65b2c58757a3e03f8b922ccb"
                                        "order": 2,
                                        "unblockDuration": 16,
                                        "name": "missed loan payment once",
                                        "canGetLoan": true
                                    },
                                    "message" : "Creditworthiness added"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Creditworthiness creation error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Creditworthiness with name already exists",
                            description = "Creditworthiness with name already exists",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Creditworthiness with name already exists"
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
    public ResponseEntity<Response> addCreditworthiness(@RequestBody CreditworthinessDto creditworthinessDto) {
        Creditworthiness creditworthiness = creditworthinessMapper.mapCreditworthinessDtoToCreditworthiness(creditworthinessDto);
        Creditworthiness savedCreditworthiness = creditworthinessService.addCreditworthiness(creditworthiness);
        CreditworthinessDto savedCreditworthinessDto = creditworthinessMapper.mapCreditworthinessToCreditworthinessDto(savedCreditworthiness);
        Response response = Response.getSuccessResponse(savedCreditworthinessDto, "Creditworthiness added");
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    @Operation(
        description = "This is admin endpoint for getting all creditworthiness states",
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
                description = "Success response for getting all creditworthiness states request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting all creditworthiness states request",
                            description = "Success response for getting all creditworthiness states request",
                            value = """
                                {
                                    "status": "success",
                                    "value": [
                                        {
                                            "id": "65e9bdb8b113ef347b0e4f03",
                                            "order": -1,
                                            "unblockDuration": -1,
                                            "name": "Default",
                                            "canGetLoan": true
                                        },
                                        {
                                            "id": "65f1bec08cc79b40bcbd9d83",
                                            "order": 0,
                                            "unblockDuration": 3,
                                            "name": "skipped payment",
                                            "canGetLoan": true
                                        },
                                        {
                                            "id": "65f1bee48cc79b40bcbd9d85",
                                            "order": 1,
                                            "unblockDuration": 6,
                                            "name": "blocked",
                                            "canGetLoan": false
                                        }
                                    ],
                                    "message": "Creditworthinesses are returned"
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
    public ResponseEntity<Response> getCreditworthinesses() {
        List<Creditworthiness> creditworthinesses = creditworthinessService.getAllCreditworthinesses();
        List<CreditworthinessDto> creditworthinessDtos = creditworthinesses
            .stream()
            .map(creditworthinessMapper::mapCreditworthinessToCreditworthinessDto)
            .toList();
        Response response = Response.getSuccessResponse(creditworthinessDtos, "Creditworthinesses are returned");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{creditworthinessId}")
    @Operation(
        description = "This is admin endpoint for getting creditworthiness state by id",
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
                name = "creditworthinessId",
                description = "creditworthinessId for getting saved creditworthiness by it",
                examples = {
                    @ExampleObject(
                        name = "creditworthinessId",
                        value = "65e9bdb8b113ef347b0e4f03"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for getting creditworthiness state by id request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting creditworthiness state by id request",
                            description = "Success response for getting creditworthiness state by id request",
                            value = """
                                {
                                    "status": "success",
                                    "value": {
                                            "id": "65e9bdb8b113ef347b0e4f03",
                                            "order": -1,
                                            "unblockDuration": -1,
                                            "name": "Default",
                                            "canGetLoan": true
                                        },
                                    "message": "Creditworthiness returned"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Creditworthiness getting error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Creditworthiness by provided id not found",
                            description = "Creditworthiness by provided id not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Creditworthiness by provided id not found"
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
    public ResponseEntity<Response> getCreditworthinessById(@PathVariable String creditworthinessId) {
        Creditworthiness creditworthiness = creditworthinessService.getCreditworthinessById(creditworthinessId);
        CreditworthinessDto creditworthinessDto = creditworthinessMapper.mapCreditworthinessToCreditworthinessDto(creditworthiness);
        Response response = Response.getSuccessResponse(creditworthinessDto, "Creditworthiness returned");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{creditworthinessId}")
    @Operation(
        description = "This is admin endpoint for getting creditworthiness state by id",
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
                name = "creditworthinessId",
                description = "creditworthinessId for updating saved creditworthiness by it",
                examples = {
                    @ExampleObject(
                        name = "creditworthinessId",
                        value = "65e9bdb8b113ef347b09bbc"
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
                schema = @Schema(implementation = CreditworthinessDto.class),
                examples = {
                    @ExampleObject(
                        name = "update creditworthiness state by id request",
                        description = "This is request body example for update creditworthiness state by id request",
                        value = """
                            {
                                "order": 2,
                                "unblockDuration": 22,
                                "name": "missed loan payment three times",
                                "canGetLoan": false
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for update creditworthiness state by id request",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for update creditworthiness state by id request",
                            description = "Success response for update creditworthiness state by id request",
                            value = """
                                {
                                    "status": "success",
                                    "value": {
                                            "id": "65e9bdb8b113ef347b0e4f03",
                                            "order": 2,
                                            "unblockDuration": 22,
                                            "name": "missed loan payment three times",
                                            "canGetLoan": false
                                        },
                                    "message": "Creditworthiness updated"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Creditworthiness option updating error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Creditworthiness by provided id not found",
                            description = "Creditworthiness by provided id not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Creditworthiness by provided id not found"
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
    public ResponseEntity<Response> updateCreditworthinessById(@PathVariable String creditworthinessId, @RequestBody CreditworthinessDto creditworthinessDto) {
        Creditworthiness creditworthiness = creditworthinessMapper.mapCreditworthinessDtoToCreditworthiness(creditworthinessDto);
        Creditworthiness updatedCreditworthiness = creditworthinessService.updateCreditworthiness(creditworthinessId, creditworthiness);
        CreditworthinessDto updatedCreditworthinessDto = creditworthinessMapper.mapCreditworthinessToCreditworthinessDto(updatedCreditworthiness);
        Response response = Response.getSuccessResponse(updatedCreditworthinessDto, "Creditworthiness updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{creditworthinessId}")
    @Operation(
        description = "This is admin endpoint for deleting creditworthiness state by id",
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
                name = "creditworthinessId",
                description = "creditworthinessId for deleting saved creditworthiness by it",
                examples = {
                    @ExampleObject(
                        name = "creditworthinessId",
                        value = "65e9bdb8b113ef347b09bbc"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for deleting saved creditworthiness by id",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for deleting saved creditworthiness by id",
                            description = "Success response for deleting saved creditworthiness by id",
                            value = """
                                {
                                    "status": "success",
                                    "value": null,
                                    "message": "Creditworthiness deleted"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Creditworthiness deletion error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Creditworthiness by provided id not found",
                            description = "Creditworthiness by provided id not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Creditworthiness by provided id not found"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Creditworthiness is in use",
                            description = "Creditworthiness is in use",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Creditworthiness is in use"
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
    public ResponseEntity<Response> deleteCreditworthinessById(@PathVariable String creditworthinessId) {
        creditworthinessService.deleteCreditworthiness(creditworthinessId);
        Response response = Response.getSuccessResponse("Creditworthiness deleted");
        return ResponseEntity.ok(response);
    }
}
