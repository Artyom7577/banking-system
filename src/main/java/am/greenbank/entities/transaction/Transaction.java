package am.greenbank.entities.transaction;

import am.greenbank.entities.account.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transaction")
public class Transaction {
    @Id
    private String id;
    private TransactionEntity from;
    private TransactionEntity to;
    private Double amount;
    private Currency currency;
    private String description;
    private LocalDateTime date;
    private Boolean done;
}
