package am.greenbank.helpers.mappers;

import am.greenbank.dtos.DepositDto;
import am.greenbank.entities.deposit.Deposit;
import am.greenbank.requests.CreateDepositRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DepositMapper {
    public Deposit mapCreateDepositRequestToDeposit(CreateDepositRequest createDepositRequest) {
        return Deposit
            .builder()
            .userId(createDepositRequest.getUserId())
            .amount(createDepositRequest.getAmount())
            .currency(createDepositRequest.getCurrency())
            .percent(createDepositRequest.getPercent())
            .depositName(createDepositRequest.getDepositName())
            .duration(createDepositRequest.getDuration())
            .from(createDepositRequest.getFrom())
            .build();
    }

    public DepositDto mapDepositToDepositDto(Deposit deposit) {
        return DepositDto
            .builder()
            .id(deposit.getId())
            .userId(deposit.getUserId())
            .amount(deposit.getAmount())
            .startDate(deposit.getStartDate())
            .endDate(deposit.getEndDate())
            .status(deposit.getStatus())
            .from(deposit.getFrom())
            .duration(deposit.getDuration())
            .currency(deposit.getCurrency())
            .percent(deposit.getPercent())
            .depositName(deposit.getDepositName())
            .build();
    }
}
