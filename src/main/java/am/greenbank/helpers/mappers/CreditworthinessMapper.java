package am.greenbank.helpers.mappers;

import am.greenbank.dtos.CreditworthinessDto;
import am.greenbank.entities.Creditworthiness;
import org.springframework.stereotype.Component;

@Component
public class CreditworthinessMapper {
    public Creditworthiness mapCreditworthinessDtoToCreditworthiness(CreditworthinessDto creditworthinessDto) {
        return Creditworthiness
            .builder()
            .id(creditworthinessDto.getId())
            .order(creditworthinessDto.getOrder())
            .unblockDuration(creditworthinessDto.getUnblockDuration())
            .name(creditworthinessDto.getName())
            .canGetLoan(creditworthinessDto.getCanGetLoan())
            .build();
    }

    public CreditworthinessDto mapCreditworthinessToCreditworthinessDto(Creditworthiness creditworthiness) {
        return CreditworthinessDto
            .builder()
            .id(creditworthiness.getId())
            .order(creditworthiness.getOrder())
            .unblockDuration(creditworthiness.getUnblockDuration())
            .name(creditworthiness.getName())
            .canGetLoan(creditworthiness.isCanGetLoan())
            .build();
    }
}
