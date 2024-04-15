package am.greenbank.requests;

import am.greenbank.entities.Option;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDepositTypeRequest {
    @NotBlank
    private String name;
    @NotEmpty
    private List<Option> options;
}
