package am.greenbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TemplateDto {
    private String id;
    private String userId;
    private String description;
    private Double amount;
    private TransactionEntityDto from;
    private TransactionEntityDto to;
}
