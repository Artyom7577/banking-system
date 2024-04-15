package am.greenbank.helpers.mappers;

import am.greenbank.dtos.LoanTypeDto;
import am.greenbank.entities.loan.LoanType;
import am.greenbank.requests.CreateLoanTypeRequest;
import org.springframework.stereotype.Component;

@Component
public class LoanTypeMapper {

    public LoanType mapCreateLoanTypeRequestToLoan(CreateLoanTypeRequest createLoanTypeRequest) {
        return LoanType
            .builder()
            .name(createLoanTypeRequest.getName())
            .options(createLoanTypeRequest.getOptions())
            .available(true)
            .build();
    }

    public LoanTypeDto mapLoanTypeToLoanTypeDto(LoanType loanType) {
        return LoanTypeDto
            .builder()
            .id(loanType.getId())
            .name(loanType.getName())
            .options(loanType.getOptions())
            .available(loanType.isAvailable())
            .build();
    }
}
