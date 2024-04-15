package am.greenbank.requests;

import am.greenbank.helpers.validations.DayOfYearValidation;
import am.greenbank.helpers.validations.UserAgeValidation;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Firstname can not be blank")
    @Pattern(regexp = "^[A-Z][\\p{L} .'-]*$", message = "Firstname should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Firstname length should be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Lastname can not be blank")
    @Pattern(regexp = "^[A-Z][\\p{L} .'-]*$", message = "Lastname should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Lastname length should be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email length should be at most 100 characters")
    private String email;

    @NotNull(message = "Date of birth can not be null")
    @DayOfYearValidation(message = "birthday should be valid day of year")
    @UserAgeValidation(min = 18, max = 80, message = "User age must be between 18 and 80")
    private String birthday;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;

    @NotBlank(message = "Phone can not be blank")
    @Size(min = 12, max = 12, message = "Armenian phone number length should be 12 characters")
    @Pattern(regexp = "^\\+374[0-9]{8}$", message = "Phone number must be a valid Armenian phone number")
    private String phone;
}
