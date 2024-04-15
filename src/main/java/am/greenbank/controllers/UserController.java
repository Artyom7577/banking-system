package am.greenbank.controllers;

import am.greenbank.dtos.ImageDto;
import am.greenbank.dtos.UserDto;
import am.greenbank.entities.image.Image;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.helpers.mappers.ImageMapper;
import am.greenbank.helpers.mappers.UserMapper;
import am.greenbank.requests.ChangePasswordRequest;
import am.greenbank.requests.SendEmailRequest;
import am.greenbank.requests.UpdateEmailRequest;
import am.greenbank.requests.VerifyRequest;
import am.greenbank.responses.SendEmailResponse;
import am.greenbank.responses.Response;
import am.greenbank.services.ImageService;
import am.greenbank.services.UserService;
import am.greenbank.services.VerificationNumberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User Description")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ImageService imageDataService;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;
    private final VerificationNumberService verificationNumberService;

    @GetMapping("/{userId}")
    @Operation(
        description = "Fetches the profile of a specific user. Accessible by the user themselves and admins.",
        parameters = {
            @Parameter(
                name = "userId",
                description = "user id to get user by it (Change id! It is random id)",
                example = "65b2c58646a3e03f8b922bba",
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
                description = "Successfully user data returned",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : {
                                        "id" : "userID"
                                        "firstName": "Clark'",
                                        "lastName": "Kent",
                                        "email": "clarkkent@gmail.com",
                                        "birthday": "29-02-1996",
                                        "password": "clarkkent",
                                        "phone": "+37414717322"
                                        "img" : "userImage or null"
                                        "accounts": [
                                            "accountId1",
                                            "accountId2",
                                            "accountId3"
                                        ]
                                        "cards": [
                                            "cardId1",
                                            "cardId2",
                                            "cardId3"
                                        ]
                                    },
                                    "message" : "user successfully registered"
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

//    @PreAuthorize("@userService.getUserById(#userId).username == principal.username or hasRole('ADMIN')")
    public ResponseEntity<Response> getUserById(@PathVariable String userId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(userId) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }

        User user = userService.getUserById(userId);
        UserDto userDto = userMapper.mapUserToUserDto(user);
        Response response = Response.getSuccessResponse(userDto, "user data");
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    @Operation(
        description = "Fetches the profiles of all users. Accessible by the admins.",
        parameters = @Parameter(
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
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully user data returned",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : [
                                        {
                                            "id" : "userId",
                                            "firstName": "Clark'",
                                            "lastName": "Kent",
                                            "email": "clarkkent@gmail.com",
                                            "birthday": "29-02-1996",
                                            "password": "clarkkent",
                                            "phone": "+37414717322"
                                            "img" : "userImage or null"
                                            "accounts": [
                                                "accountId1",
                                                "accountId2",
                                                "accountId3"
                                            ]
                                            "cards": [
                                                "cardId1",
                                                "cardId2",
                                                "cardId3"
                                            ]
                                        }
                                    ],
                                    "message" : "user successfully registered"
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
                            description = "when user without admin role tries to access all users data",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "User cannot access this endpoint"
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response> getAllUsers(Authentication authentication) {
        List<User> users = userService.getAllUsers();
        List<UserDto> list = users.stream().map(userMapper::mapUserToUserDto).toList();
        Response response = Response.getSuccessResponse(list, "All users are successfully returned");
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{userId}")
    @Operation(
        description = "Updates the profile for a specific user. Accessible by the user themselves and admins.",

        parameters = {
            @Parameter(
                name = "userId",
                description = "user id to get user by it (Change id! It is random id)",
                example = "65b2c58646a3e03f8b922bba",
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
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(
                    type = "object",
                    properties = {
                        @StringToClassMapItem(key = "firstName", value = String.class),
                        @StringToClassMapItem(key = "lastName", value = String.class),
                        @StringToClassMapItem(key = "phone", value = String.class),
                        @StringToClassMapItem(key = "image", value = MultipartFile.class)
                    }
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully user data returned",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : {
                                        "id" : "userID"
                                        "firstName": "Clark'",
                                        "lastName": "Kent",
                                        "email": "clarkkent@gmail.com",
                                        "birthday": "29-02-1996",
                                        "password": "clarkkent",
                                        "phone": "+37422773322"
                                        "img" : "userImage or null"
                                        "accounts": [
                                            "accountId1",
                                            "accountId2",
                                            "accountId3"
                                        ]
                                        "cards": [
                                            "cardId1",
                                            "cardId2",
                                            "cardId3"
                                        ]
                                    },
                                    "message" : "user updated"
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
                                    "message" : "Invalid credentials - Firstname should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots\\nLastname should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots\\nArmenian phone number length should be 12 characters"
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
//    @PreAuthorize("#userId == userService.getUserByEmail(principal.username).id or hasRole('ADMIN')")
    public ResponseEntity<Response> updateUser(
        @PathVariable String userId,
        @RequestPart(value = "firstName", required = false)
        @Valid
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name should contain only letters, spaces, apostrophes, hyphens, and dots")
        @Size(min = 2, max = 50, message = "Name length should be between 2 and 50 characters")
        String firstName,
        @RequestPart(value = "lastName", required = false)
        @Valid
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Surname should contain only letters, spaces, apostrophes, hyphens, and dots")
        @Size(min = 2, max = 50, message = "Surname length should be between 2 and 50 characters")
        String lastName,
        @RequestPart(value = "phone", required = false)
        @Valid
        @Size(min = 12, max = 12, message = "Armenian phone number length should be 12 characters")
        @Pattern(regexp = "^\\+374[0-9]{8}$", message = "Phone number must be a valid Armenian phone number")
        String phone,
        @RequestPart(value = "image", required = false) MultipartFile image,
        Authentication authentication
    ) throws IOException {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(userId) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
        String imageId = null;
        if (image != null) {
            imageId = imageDataService.uploadImage(image, userId);
        }
        //        User user = userMapper.mapUpdateUserRequestToUser(request);
        User user = User
            .builder()
            .firstName(firstName)
            .lastName(lastName)
            .phone(phone)
            .img(imageId)
            .build();
        User updatedUser = userService.updateUser(userId, user);
        UserDto updatedUserDto = userMapper.mapUserToUserDto(updatedUser);
        Response response = Response.getSuccessResponse(updatedUserDto, "user updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(
        description = "Deletes a specific user profile. Accessible by admins and user only.",
        parameters = {
            @Parameter(
                name = "userId",
                description = "user id to delete user by it (Change id! It is random id)",
                example = "65b2c58646a3e03f8b922bba",
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
                responseCode = "204",
                description = "Successfully user data deleted",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : null,
                                    "message" : "user deleted"
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
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable String userId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getId().equals(userId) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
        userService.deleteUser(userId);
        Response response = Response.getSuccessResponse("user deleted");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/image/{imageId}")
    @Operation(
        description = "Fetches the profile of a specific user. Accessible by the user themselves and admins.",
        parameters = {
            @Parameter(
                name = "imageId",
                description = "image id to get image by it (Change id! It is random id)",
                example = "65b2c58646a3e03f8b922bba",
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
                description = "Image successfully returned",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "user returned",
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : {
                                        "id" : "65b2c58646a3e03f8b922bba",
                                        "name" : "user.png",
                                        "type" : "image/png",
                                        "imageData" : "image data"
                                    },
                                    "message" : "Image successfully returned"
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
                            name = "Image Not Found",
                            description = "when image by id not found",
                            value = """
                                    {
                                      "status" : "error",
                                      "value" : null,
                                      "message" : "Image Not Found"
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
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> getImageInfoByImageId(@PathVariable String imageId, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.getImg().equals(imageId) && !principal.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("User can access to data connected to him");
        }
        Image image = imageDataService.getImageById(imageId);
        ImageDto imageDto = imageMapper.mapImageToImageDto(image);
        Response imageSuccessfullyReturned = Response.getSuccessResponse(imageDto, "Image successfully returned");
        return ResponseEntity.ok(imageSuccessfullyReturned);
    }

    @PatchMapping("/changePassword")
    @Operation(
        description = "Change user password",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Password changed successfully",
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
                                            "id": "65b2c58646a3e03f8b922bba",
                                            "username": "exampleUser",
                                            "email": "user@example.com",
                                            "roles": ["USER"]
                                        },
                                        "message": "Password changed successfully"
                                    }
                                """
                        )
                    }
                )
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
//    @PreAuthorize("#userId == userService.getUserByEmail(principal.username).id or hasRole('ADMIN')")
    public ResponseEntity<Response> changePassword(
        @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
        Authentication authentication
    ) {
        User principal = (User) authentication.getPrincipal();

        User user = userService.changePassword(principal.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmNewPassword());

        UserDto userDto = userMapper.mapUserToUserDto(user);

        Response response = Response.getSuccessResponse(userDto, "Password changed successfully");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/changeEmail")
    @Operation(
        description = "Change user email and send confirmation email to the old email",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email changed successfully, confirmation email sent",
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
                                            "userId": "65b2c58646a3e03f8b922bba"
                                        },
                                        "message": "Email changed successfully, confirmation email sent"
                                    }
                                """
                        )
                    }
                )
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> changeEmailSendMessageToOldEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        String userId = userService.changeEmailSendMessageToOldEmail(sendEmailRequest.getEmail());

        Response response = Response.getSuccessResponse(new SendEmailResponse(userId), "email send successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Verify the number sent to the old email during the change email process
     *
     * @param verifyRequest The request object containing the user ID and verification number
     * @return ResponseEntity with the user ID and success message
     */
    @PostMapping("/changeEmail")
    @Operation(
        description = "Verify the number sent to the old email during the change email process",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Number verified successfully",
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
                                            "userId": "65b2c58646a3e03f8b922bba"
                                        },
                                        "message": "Number verified successfully"
                                    }
                                """
                        )
                    }
                )
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> changeEmailVerifyMessageSentToOldEmail(@RequestBody @Valid VerifyRequest verifyRequest) {
        String savedUserId = verificationNumberService.verifyNumberSentToOldEmailInChangeEmail(
            verifyRequest.getUserId(), verifyRequest.getNumber()
        );

        Response response = Response.getSuccessResponse(new SendEmailResponse(savedUserId), "Number verified successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Send a confirmation email to the new email after changing the email address
     *
     * @param sendEmailRequest The request object containing the new email and user ID
     * @return ResponseEntity with the user ID and success message
     */
    @PatchMapping("/newEmail")
    @Operation(
        description = "Send confirmation email to the new email after changing the email address",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email confirmation sent successfully",
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
                                            "userId": "65b2c58646a3e03f8b922bba"
                                        },
                                        "message": "Email confirmation sent successfully"
                                    }
                                """
                        )
                    }
                )
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> changeEmailSendMessageToNewEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        String userId = userService.changeEmailSendMessageToNewEmail(sendEmailRequest.getEmail(), sendEmailRequest.getUserId());

        Response response = Response.getSuccessResponse(new SendEmailResponse(userId), "email send successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Verify the number sent to the new email during the change email process and update the email address
     *
     * @param updateEmailRequest The request object containing the user ID, verification number, and new email
     * @return ResponseEntity with the user ID and success message
     */
    @PostMapping("/newEmail")
    @Operation(
        description = "Verify the number sent to the new email during the change email process and update the email address",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Number verified successfully",
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
                                            "userId": "65b2c58646a3e03f8b922bba"
                                        },
                                        "message": "Number verified successfully"
                                    }
                                """
                        )
                    }
                )
            )
        },
        security = {
            @SecurityRequirement(
                name = "bearerAuth"
            )
        }
    )
    public ResponseEntity<Response> changeEmailVerifyNewEmailAndUpdate(@RequestBody UpdateEmailRequest updateEmailRequest) {
        String savedUserId = verificationNumberService.verifyNumberSentToNewEmailInChangeEmailAndUpdateUserEmail(
            updateEmailRequest.getUserId(), updateEmailRequest.getNumber(), updateEmailRequest.getNewEmail()
        );

        Response response = Response.getSuccessResponse(new SendEmailResponse(savedUserId), "Number verified successfully");

        return ResponseEntity.ok(response);
    }
}
