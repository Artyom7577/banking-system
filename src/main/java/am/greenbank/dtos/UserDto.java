package am.greenbank.dtos;

import am.greenbank.entities.user.UserRole;
import am.greenbank.helpers.validations.UserAgeValidation;
import am.greenbank.responses.Value;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UserDto implements Value {
    private String id;

    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name should contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Name length should be between 2 and 50 characters")
    private String firstName;

    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "surname should contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "surname length should be between 2 and 50 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email length should be at most 100 characters")
    private String email;

    @UserAgeValidation(min = 18, max = 80, message = "User age must be between 18 and 80")
    private String birthday;

    @Size(min = 12, max = 12, message = "Armenian phone number length should be 12 characters")
    @Pattern(regexp = "^\\+374[0-9]{8}$", message = "Phone number must be a valid Armenian phone number")
    private String phone;

    private String img;

    private UserRole role;

    private List<String> accounts;
    private List<String> cards;
}
