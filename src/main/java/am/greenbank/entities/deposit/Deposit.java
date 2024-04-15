package am.greenbank.entities.deposit;

import am.greenbank.entities.account.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "deposit")
public class Deposit {
    @Id
    private String id;
    private String userId;
    private Double amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private DepositStatus status;
    private String from;
    private Integer duration;
    private Double percent;
    private Currency currency;
    private String depositName;
}
