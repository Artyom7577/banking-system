package am.greenbank.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CreditworthinessDto {
    private String id;
    private Integer order;
    private Integer unblockDuration;
    private String name;
    private Boolean canGetLoan;
}
