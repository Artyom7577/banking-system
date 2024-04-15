package am.greenbank.requests;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name should contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Name length should be between 2 and 50 characters")
    private String firstName;
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "surname should contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Surname length should be between 2 and 50 characters")
    private String lastName;
    @Size(min = 12, max = 12, message = "Armenian phone number length should be 12 characters")
    @Pattern(regexp = "^\\+374[0-9]{8}$", message = "Phone number must be a valid Armenian phone number")
    private String phone;
}
