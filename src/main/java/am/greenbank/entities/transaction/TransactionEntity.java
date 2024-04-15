package am.greenbank.entities.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TransactionEntity {
    private String number;
    private String userId;
    private TransactionType type;
}
