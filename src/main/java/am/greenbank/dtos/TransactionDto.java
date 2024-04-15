package am.greenbank.dtos;

import am.greenbank.responses.Value;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto implements Value {
    private String id;
    private TransactionEntityDto from;
    private TransactionEntityDto to;
    private Double amount;
    private String description;
    private String date;
    private Boolean done;
}
