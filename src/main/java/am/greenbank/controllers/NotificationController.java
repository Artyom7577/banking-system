package am.greenbank.controllers;

import am.greenbank.dtos.NotificationDto;
import am.greenbank.entities.Notification;
import am.greenbank.entities.user.User;
import am.greenbank.helpers.mappers.NotificationMapper;
import am.greenbank.responses.Response;
import am.greenbank.services.NotificationServie;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@Tag(name = "Notification", description = "Notification Description")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServie notificationServie;
    private final NotificationMapper notificationMapper;

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
                description = "userId for returning users notifications",
                examples = {
                    @ExampleObject(
                        name = "userId",
                        description = "randomly created userId",
                        value = "65b2c58646a3e03f8b922bba"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for getting users notifications",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting users notifications",
                            description = "Success response for getting users notifications",
                            value = """
                                {
                                    "status" : "success",
                                    "value" : [
                                        {
                                            "id": "65b2c58757a3e03f8b933cfb",
                                            "userId": "65b2c58757a3e03f8b922ccb",
                                            "message": "transaction received for account 1111222233334444 with amount 200 USD from account 5555666677778888",
                                            "time": "2024-03-15 20:10:23",
                                            "read": false
                                        },
                                        {
                                            "id": "65b2c58757a3e03f8b933cfb",
                                            "userId": "65b2c58757a3e03f8b922ccb",
                                            "message": "transaction doesn't sent to account 5555666677778888 with amount 200 USD because of insufficient funds from account 1111222233334444",
                                            "time": "2024-03-14 11:12:13",
                                            "read": false
                                        },
                                    ],
                                    "message" : "notifications returned successfully"
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
    public ResponseEntity<Response> getAllByUserId(@PathVariable String userId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(userId)) {
            throw new AccessDeniedException("User can access to data connected to him/her");
        }

        List<Notification> notifications = notificationServie.getAllByUserId(userId);
        List<NotificationDto> notificationDtos = notifications
            .stream()
            .map(notificationMapper::mapNotificationToNotificationDto)
            .toList();

        Response response = Response.getSuccessResponse(notificationDtos, "notifications returned successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificationId}")
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
                name = "notificationId",
                description = "notification Id for returning notification by it",
                examples = {
                    @ExampleObject(
                        name = "notificationId",
                        description = "randomly created notificationId",
                        value = "65b2c58646a3e03f8b922bba"
                    ),
                },
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Success response for getting users notifications",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Success response for getting users notifications",
                            description = "Success response for getting users notifications",
                            value = """
                                {
                                    "status" : "success",
                                    "value" : {
                                            "id": "65b2c58757a3e03f8b933cfb",
                                            "userId": "65b2c58757a3e03f8b922ccb",
                                            "message": "transaction received for account 1111222233334444 with amount 200 USD from account 5555666677778888",
                                            "time": "2024-03-15 20:10:23",
                                            "read": true
                                        },
                                    "message" : "Notification returned and read successfully"
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
                            name = "Notification Not Found",
                            description = "when notification by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Notification Not Found"
                                    }
                                """
                        ),
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
    public ResponseEntity<Response> getNotificationById(@PathVariable String notificationId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        boolean userHasNotification = user
            .getNotifications()
            .stream()
            .anyMatch(notificationIdFromList -> notificationIdFromList.equals(notificationId));

        if (!userHasNotification) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        Notification notification = notificationServie.readNotificationById(notificationId, user.getId());
        NotificationDto notificationDto = notificationMapper.mapNotificationToNotificationDto(notification);
        Response response = Response.getSuccessResponse(notificationDto, "Notification returned and read successfully");
        return ResponseEntity.ok(response);
    }
}