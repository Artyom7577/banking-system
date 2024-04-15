package am.greenbank.helpers.mappers;

import am.greenbank.dtos.LoanDto;
import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.loan.LoanStatus;
import am.greenbank.requests.CreateLoanRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

@Component
public class LoanMapper {
    public Loan mapCreateLoanRequestToLoan(CreateLoanRequest createLoanRequest) {
        return Loan
            .builder()
            .userId(createLoanRequest.getUserId())
            .amount(createLoanRequest.getAmount())
            .currency(createLoanRequest.getCurrency())
            .percent(createLoanRequest.getPercent())
            .loanName(createLoanRequest.getLoanName())
            .duration(createLoanRequest.getDuration())
            .paymentDays(Collections.emptyList())
            .paymentTransactionIds(Collections.emptyList())
            .build();
    }

    public LoanDto mapLoanToLoanDto(Loan loan) {
        return LoanDto
            .builder()
            .id(loan.getId())
            .userId(loan.getUserId())
            .amount(loan.getAmount())
            .stayedAmount(loan.getStayedAmount())
            .startDate(loan.getStartDate())
            .endDate(loan.getEndDate())
            .status(loan.getStatus())
            .currency(loan.getCurrency())
            .percent(loan.getPercent())
            .loanName(loan.getLoanName())
            .duration(loan.getDuration())
            .payment(loan.getPayment())
            .build();
    }
}
