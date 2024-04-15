package am.greenbank.controllers;

import am.greenbank.dtos.UserDto;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.VerificationNumber;
import am.greenbank.helpers.mappers.UserMapper;
import am.greenbank.requests.*;
import am.greenbank.responses.*;
import am.greenbank.services.*;
import am.greenbank.services.email.EmailSender;
import io.github.bucket4j.local.LocalBucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication Description")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final VerificationNumberService verificationNumberService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EmailSender emailSender;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final LocalBucket localBucket;

    @PostMapping("/register")
    @Operation(
        description = "Endpoint for user registration",
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
            description = "This is request body for user registration",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RegisterRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Register request",
                        description = "Register request body",
                        value = """
                            {
                              "firstName": "Clark",
                              "lastName": "Kent",
                              "email": "clarkkent@gmail.com",
                              "birthday": "29-02-1996",
                              "password": "clarkkent",
                              "phone": "+37414717322"
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "User successfully registered",
                content = @Content(
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "Success",
                            description = "Response after success for request from web",
                            value = """
                                    {
                                      "status" : "success",
                                      "value" : {
                                        "userId" : "65b901f5a4e5fe12e57736ec"
                                        }
                                      },
                                      "message" : "user successfully registered"
                                    }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "409",
                description = "User with this email or phone already exists",
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
                                    "message" : "user with this email address already exists"
                                }
                                """
                        ),
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "user with this phone already exists"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Validation or verification error",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Validation error",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Invalid credentials - Name should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots\\nName should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots\\nEmail should be valid\\nUser age must be between 18 and 80\\nPassword should have at least 6 characters\\nArmenian phone number length should be 12 characters"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "Verification error",
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "This user is registered but not verified please. Check your email to verify account"
                                }
                                """
                        )

                    }
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
                                    "message" : "Internal server error"
                                }
                                """
                        )
                    }
                )
            )
        }
    )
    public ResponseEntity<Response> register(
        @RequestHeader(name = "X-platform") String platformName,
        @RequestBody @Valid RegisterRequest userDto
    ) {
        User user = userMapper.mapRegisterUserDtoToUser(userDto);
        user.setEmail(user.getEmail().toLowerCase());
        User savedUser = authenticationService.register(user);
        Response response = Response.getSuccessResponse(new RegisterResponse(savedUser.getId()));
        response.setMessage("user registration mail successfully send");

        return ResponseEntity
            .status(201)
            .body(response);
    }

    @PostMapping("/verifyNumber")
    @Operation(
        description = "Verify user based on the provided verification number",
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
            description = "JSON object containing userId and number",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Verification request",
                        description = "Example of a verification request",
                        value = """
                            {
                                "userId": "65b901f5a4e5fe12e57736ec",
                                "number": "1234"
                            }
                            """
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully verified number",
                content = @Content(
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "web success response",
                            description = "success response for web browser",
                            value = """
                                    {
                                        "status" : "success",
                                        "value" : {
                                            "refreshToken": null,
                                            "accessToken": "eyJhbGciOiJIUzI1NiJ9.."
                                            "userDto": {
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
                                        },
                                        "message" : "user successfully registered"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "ios success response",
                            description = "success response for ios",
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : {
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..",
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.."
                                        "userDto": {
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
                                    },
                                    "message" : "user successfully registered"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "User not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "Error response",
                            description = "Response when user is not found",
                            value = """
                                {
                                    "status": "error",
                                    "value": null,
                                    "message": "User not found."
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Verification number found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "Verification number not found response",
                            description = "Response when verification number is not found",
                            value = """
                                {
                                    "status": "error",
                                    "value": null,
                                    "message": "Verification number not found"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Verification number expired",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "Verification number expired response",
                            description = "Response when verification number has expired",
                            value = """
                                {
                                    "status": "error",
                                    "value": null,
                                    "message": "Number has already expired!"
                                }
                                """
                        )
                    }
                )
            )
        }
    )
    public ResponseEntity<Response> verifyNumber(
        @RequestBody @Valid VerifyRequest verifyRequest,
        HttpServletResponse httpServletResponse,
        @RequestHeader(name = "X-platform") String platformName
    ) {
        verificationNumberService.verifyNumber(verifyRequest);

        User userById = userService.getUserById(verifyRequest.getUserId());

        Response response = getResponse(platformName, httpServletResponse, userById);
        response.setMessage("Number verified successfully. User enabled.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resetVerifyNumber/{userId}")
    @Operation(
        description = "Reset verification number for the user",
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
                description = "Successfully reset verification number",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "Success response",
                            description = "Response after successfully resetting the verification number",
                            value = """
                                {
                                    "status": "success",
                                    "value" : null,
                                    "message": "Successfully send"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "User not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseEntity.class),
                    examples = {
                        @ExampleObject(
                            name = "Error response",
                            description = "Response when user is not found",
                            value = """
                                {
                                    "status": "error",
                                    "value": null,
                                    "message": "User not found."
                                }
                                """
                        )
                    }
                )
            )
        }
    )
    public ResponseEntity<Response> resetVerifyNumber(
        @PathVariable String userId,
        @RequestHeader(name = "X-platform") String platformName
    ) {
        User userById = userService.getUserById(userId);

        verificationNumberService.deleteNumbersByUserId(userId);

        VerificationNumber number = verificationNumberService.createNumber(userService.getUserById(userId));

        emailSender.sendEmail(userById.getEmail(), number.getNumber());

        return ResponseEntity.status(200).body(Response.getSuccessResponse("Successfully send"));
    }

    @PostMapping("/login")
    @Operation(
        description = "Endpoint for user login",
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
            description = "This is request body for user registration",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    type = "LoginRequest",
                    example = """
                        {
                          "email": "clarkkent@gmail.com",
                          "password": "clarkkent"
                        }
                        """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully user login",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "web success response",
                            description = "success response for web browser",
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : {
                                        "refreshToken": null,
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.."
                                        "userDto": {
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
                                    },
                                    "message" : "user successfully registered"
                                }
                                """
                        ),
                        @ExampleObject(
                            name = "ios success response",
                            description = "success response for ios",
                            value = """ 
                                {
                                    "status" : "success",
                                    "value" : {
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9..",
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.."
                                        "userDto": {
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
                                    },
                                    "message" : "user successfully registered"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Validation error",
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
                                    "message" : "Invalid credentials - Email should be valid\\nPassword should have at least 6 characters"
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
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            value = """ 
                                {
                                    "status" : "error",
                                    "value" : null,
                                    "message" : "Bad credentials"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
                                    "message" : "Internal server error"
                                }
                                """
                        )
                    }
                )
            )
        }
    )

    public ResponseEntity<Response> authenticate(
        @RequestHeader(name = "X-platform") String platformName,
        @RequestBody @Valid LoginRequest request,
        HttpServletResponse httpServletResponse
    ) {

        User user = authenticationService.authenticate(request, httpServletResponse);
        Response response = getResponse(platformName, httpServletResponse, user);
        response.setMessage("user successfully logged in");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    @Operation(

        parameters = {
            @Parameter(
                name = "Authorization",
                description = "Refresh token from Authorization only for ios platform",
                example = "string",
                in = ParameterIn.HEADER
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
                description = "Successfully user logout",
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
                                    "message" : "user successfully logged out"
                                  }
                                """
                        )
                    }
                )

            ),
            @ApiResponse(
                responseCode = "401",
                description = "No refresh token or refresh token is not valid",
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
                                    "message" : "Refresh token is not valid"
                                  }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
                                    "message" : "Internal server error"
                                }
                                """
                        )
                    }
                )
            )
        }
    )
    public ResponseEntity<Response> logout(
        @Parameter(hidden = true) @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @RequestHeader(name = "X-platform") String platformName
    ) {
        String refreshToken = getRefreshToken(cookieRefreshToken, authorizationHeader, platformName);
        System.out.println(refreshToken);

        authenticationService.logout(refreshToken);
        Response response = Response.getSuccessResponse("user successfully logged out");

        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/refresh")
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
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully user logout",
                content = @Content(
                    schema = @Schema(
                        implementation = ResponseEntity.class
                    ),
                    examples = {
                        @ExampleObject(
                            name = "Web refresh token success",
                            description = "refresh access token response for web browser",
                            value = """
                                    {
                                        "status" : "success",
                                        "value": {
                                                "refreshToken": null,
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.."
                                        },
                                        "message" : "access token refreshed"
                                    }
                                """
                        ),
                        @ExampleObject(
                            name = "IOS refresh token success",
                            description = "refresh access token response for ios",
                            value = """
                                    {
                                        "status" : "success",
                                        "value": {
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9..",
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.."
                                        },
                                        "message" : "access token refreshed"
                                    }
                                """
                        )

                    }
                )

            ),
            @ApiResponse(
                responseCode = "401",
                description = "No refresh token or refresh token is not valid",
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
                                    "message" : "Refresh token is not valid"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
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
                                    "message" : "Internal server error"
                                }
                                """
                        )
                    }
                )
            )
        }

    )
    public ResponseEntity<Response> refreshToken(
        @Parameter(hidden = true) @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @RequestHeader(name = "X-platform") String platformName
    ) {
        String refreshToken = getRefreshToken(cookieRefreshToken, authorizationHeader, platformName);
        System.out.println(refreshToken);

        String accessToken = authenticationService.refreshAccessToken(refreshToken);

        RefreshTokenResponse refreshTokenResponse = RefreshTokenResponse
            .builder()
            .accessToken(accessToken)
            .build();

        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            refreshTokenResponse.setRefreshToken(refreshToken);
        }

        Response response = Response.getSuccessResponse(refreshTokenResponse, "access token refreshed");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/forgetPassword")
    public ResponseEntity<Response> forgetPasswordEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        String userId = authenticationService.forgetPasswordEmail(sendEmailRequest.getEmail());

        Response response = Response.getSuccessResponse(new SendEmailResponse(userId), "email send successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Response> forgetPasswordVerifyNumber(
        @RequestBody @Valid VerifyRequest verifyRequest
    ) {
        String savedUserId = verificationNumberService.verifyNumberForForgetPassword(
            verifyRequest.getUserId(), verifyRequest.getNumber()
        );

        Response response = Response.getSuccessResponse(new SendEmailResponse(savedUserId), "Number verified successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Response> resetPassword(
        @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        userService.resetPassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getPassword());

        Response response = Response.getSuccessResponse("Password Changed Successfully");

        return ResponseEntity.ok(response);
    }

    private static String getRefreshToken(String cookieRefreshToken, String authorizationHeader, String platformName) {
        if (platformName.equals("web")) {
            return cookieRefreshToken;
        } else {
            return authorizationHeader.substring("Bearer ".length());
        }
    }

    private void setRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) jwtService.getRefreshTokenValidityInMilliseconds() / 1000);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    private Response getResponse(String platformName, HttpServletResponse httpServletResponse, User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveToken(refreshToken, user);

        UserDto userDto = userMapper.mapUserToUserDto(user);

        var authenticationResponse = AuthenticationResponse
            .builder()
            .userDto(userDto)
            .accessToken(accessToken)
            .build();

        if (platformName.equals("web")) {
            setRefreshTokenInCookie(httpServletResponse, refreshToken);
        } else {
            authenticationResponse.setRefreshToken(refreshToken);
        }

        return Response.getSuccessResponse(authenticationResponse);
    }
}
