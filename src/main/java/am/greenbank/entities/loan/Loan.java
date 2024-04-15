package am.greenbank.entities.loan;

import am.greenbank.entities.account.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "loans")
public class Loan {
    @Id
    private String id;
    private String userId;
    private Double amount;
    private Double stayedAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LoanStatus status;
    private Currency currency;
    private Double percent;
    private Double dailyInterestRate;
    private String loanName;
    private Integer duration;
    private Double payment;
    private List<LocalDate> paymentDays;
    private List<String> paymentTransactionIds;
}
