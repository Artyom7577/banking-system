package am.greenbank.dtos;

import am.greenbank.entities.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionEntityDto {
    private String number;
    private TransactionType type;
}