package am.greenbank.entities.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "template")
public class Template {
    @Id
    private String id;
    private String userId;
    private String description;
    private Double amount;
    private TransactionEntity from;
    private TransactionEntity to;
    private boolean deleted;
}
