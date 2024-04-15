package am.greenbank.controllers;

import am.greenbank.dtos.TemplateDto;
import am.greenbank.entities.transaction.Template;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.helpers.mappers.TemplateMapper;
import am.greenbank.responses.Response;
import am.greenbank.services.TemplateService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@Tag(name = "Template", description = "Template Description")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;
    private final TemplateMapper templateMapper;

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
                schema = @Schema(implementation = TemplateDto.class),
                examples = {
                    @ExampleObject(
                        name = "create template request",
                        description = "This is request body example for template creation",
                        value = """
                            {
                                "userId": "65b2c58757a3e03f8b922ccb",
                                "description": "template for money transfer to someone",
                                "to": {
                                    "number": "1111222233334444",
                                    "type": "ACCOUNT"
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
                description = "Success response for adding transaction template",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for users updated template transactions",
                            description = "Success response for users saved template transactions",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : null,
                                    "message" : "Template saved successfully"
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
    public ResponseEntity<Response> addTemplate(@RequestBody TemplateDto templateDto) {
        Template template = templateMapper.mapTemplateDtoToTemplate(templateDto);
        Template saved = templateService.save(template);
        Response response = Response.getSuccessResponse("Template saved successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{templateId}")
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
                name = "templateId",
                description = "templateId for updating users saved template transactions by it",
                examples = {
                    @ExampleObject(
                        name = "templateId",
                        value = "65b2c58646a3e03f8b922bba"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for users transaction template updating",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for users updated template transactions",
                            description = "Success response for users saved template transactions",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : null,
                                    "message" : "Template updated successfully"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Template update error response",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Template by provided id not found",
                            description = "Template by provided id not found",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Template by provided id not found"
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
    public ResponseEntity<Response> updateTemplate(@PathVariable String templateId, @RequestBody TemplateDto templateDto) {
        Template template = templateMapper.mapTemplateDtoToTemplate(templateDto);
        Template updated = templateService.updateById(template, templateId);
        Response response = Response.getSuccessResponse("Template updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{templateId}")
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
                name = "templateId",
                description = "templateId for deleting users saved template transactions by it",
                examples = {
                    @ExampleObject(
                        name = "templateId",
                        value = "65b2c58646a3e03f8b922bba"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Success response for users template transaction deleting",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for users template transactions deleting",
                            description = "Success response for users saved template transactions",
                            value = """
                                {
                                    "status" : "Success",
                                    "value" : null,
                                    "message" : "Template deleted successfully"
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
    public ResponseEntity<Response> deleteTemplate(@PathVariable String templateId) {
        templateService.deleteById(templateId);
        Response response = Response.getSuccessResponse("Template deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
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
                description = "userId for returning users saved template transactions",
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
                description = "Success response for users saved template transactions",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for users saved template transactions",
                            description = "Success response for users saved template transactions",
                            value = """
                                {
                                    "status" : "success",
                                    "value" : [
                                        {
                                            "id" : "831513eygdfiyb23r8612e2",
                                            "userId" : "65b2c58646a3e03f8b922bba",
                                            "to": {
                                                "number": "4326471287431234",
                                                "type": "CARD"
                                            }
                                        }
                                    ],
                                    "message" : "Saved templates are returned"
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
    public ResponseEntity<Response> getAllSavedTransactionsByUserId(@PathVariable String userId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(userId) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        List<Template> allTemplatesByUserId = templateService.getAllTemplatesByUserId(userId);
        List<TemplateDto> allTemplateDtosByUserId = allTemplatesByUserId
            .stream()
            .map(templateMapper::mapSaveTransactionToSaveTransactionDto)
            .toList();
        Response response = Response.getSuccessResponse(allTemplateDtosByUserId, "Saved templates are returned");
        return ResponseEntity.ok(response);
    }

}
