package am.greenbank.dtos;

import am.greenbank.entities.account.Currency;
import am.greenbank.entities.loan.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {
    private String id;
    private String userId;
    private Double amount;
    private Double stayedAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LoanStatus status;
    private Currency currency;
    private Double percent;
    private String loanName;
    private Integer duration;
    private Double payment;
}
