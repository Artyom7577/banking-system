package am.greenbank.dtos;

import am.greenbank.entities.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanTypeDto {
    private String id;
    private String name;
    private List<Option> options;
    private boolean available;
}
