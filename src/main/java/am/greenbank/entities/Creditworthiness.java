package am.greenbank.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("creditworthiness")
public class Creditworthiness {
    @Id
    private String id;
    private int order;
    private int unblockDuration;
    private String name;
    private boolean canGetLoan;
}
