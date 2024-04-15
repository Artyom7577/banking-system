package am.greenbank.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNameRequest {
    @NotBlank(message = "Name can not be blank")
    private String name;
}
