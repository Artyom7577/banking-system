package am.greenbank.dtos;


import am.greenbank.entities.account.Currency;
import am.greenbank.entities.deposit.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositDto {
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
