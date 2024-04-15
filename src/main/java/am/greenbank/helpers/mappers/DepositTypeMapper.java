package am.greenbank.helpers.mappers;

import am.greenbank.dtos.DepositTypeDto;
import am.greenbank.requests.CreateDepositTypeRequest;
import org.springframework.stereotype.Component;
import am.greenbank.entities.deposit.DepositType;
@Component
public class DepositTypeMapper {
    public DepositType mapCreateDepositTypeRequestToDeposit(CreateDepositTypeRequest createDepositTypeRequest) {
        return DepositType
            .builder()
            .name(createDepositTypeRequest.getName())
            .options(createDepositTypeRequest.getOptions())
            .available(true)
            .build();
    }

    public DepositTypeDto mapDepositTypeToDepositTypeDto(DepositType loanType) {
        return DepositTypeDto
            .builder()
            .id(loanType.getId())
            .name(loanType.getName())
            .options(loanType.getOptions())
            .available(loanType.isAvailable())
            .build();
    }
}
