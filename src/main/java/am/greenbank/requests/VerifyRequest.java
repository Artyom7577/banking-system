package am.greenbank.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequest {
    @NotBlank(message = "User ID must not be blank")
    private String userId;
    @Size(min = 4, max = 4, message = "Verify number length should be 4 digit")
    @Pattern(regexp = "[0-9]{4}$", message = "Verify number must be a valid number")
    private String number;
}
