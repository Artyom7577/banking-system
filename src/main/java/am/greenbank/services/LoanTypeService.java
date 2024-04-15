package am.greenbank.services;

import am.greenbank.entities.loan.Loan;
import am.greenbank.entities.loan.LoanType;
import am.greenbank.entities.Option;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.repositories.interfaces.LoanRepository;
import am.greenbank.repositories.interfaces.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanTypeService {
    private final LoanTypeRepository loanTypeRepository;
    private final LoanRepository loanRepository;

    public LoanType createLoanType(LoanType loanType) {
        if (loanTypeRepository.findByName(loanType.getName()).isPresent()) {
            throw new LoanTypeAlreadyExistsException("Loan type with this name already exists");
        }

        return loanTypeRepository.save(loanType);
    }

    public List<LoanType> getAll() {
        return loanTypeRepository.findAll();
    }

    public void deleteByName(String loanName) {
        loanTypeRepository.findByName(loanName)
            .orElseThrow(() -> new LoanTypeNotFoundException("Loan type with name provided name not found"));
        List<Loan> allByLoanName = loanRepository.findAllByLoanName(loanName);
        if (!allByLoanName.isEmpty()) {
            throw new LoanTypeInUseException("Loan type is in use");
        }
        loanTypeRepository.deleteByName(loanName);
    }

    public LoanType addOptionToLoanType(String loanName, Option option) {
        LoanType loanType = loanTypeRepository.findByName(loanName).orElseThrow(
            () -> new LoanTypeNotFoundException("Loan type with name provided name not found")
        );

        List<Option> options = loanType.getOptions();
        if (options.contains(option)) {
            throw new OptionAlreadyExistsException("This option for this loan type already exists");
        }

        options.add(option);

        return loanTypeRepository.save(loanType);
    }

    public LoanType removeOptionToLoanType(String loanName, Option option) {
        LoanType loanType = loanTypeRepository.findByName(loanName).orElseThrow(
            () -> new LoanTypeNotFoundException("Loan type with name provided name not found")
        );

        List<Option> options = loanType.getOptions();
        if (!options.contains(option)) {
            throw new OptionNotFoundException("This option for this loan type doesn't exist");
        }

        options.remove(option);

        return loanTypeRepository.save(loanType);
    }

    //    @Transactional
    public LoanType updateLoanType(String loanName, String newLoanName, Boolean available) {
        LoanType loanType = loanTypeRepository.findByName(loanName).orElseThrow(
            () -> new LoanTypeNotFoundException("Loan type with name provided name not found")
        );

        if (loanTypeRepository.findByName(newLoanName).isPresent()) {
            throw new LoanTypeAlreadyExistsException("Loan type with this name already exists");
        }

        if (newLoanName != null && !newLoanName.equals(loanName)) {
            List<Loan> allByLoanName = loanRepository.findAllByLoanName(loanName);

            allByLoanName.forEach(loan -> loan.setLoanName(newLoanName));
            loanRepository.saveAllLoans(allByLoanName);

            loanType.setName(newLoanName);
        }

        if (available != null) {
            loanType.setAvailable(available);
        }

        return loanTypeRepository.save(loanType);
    }
}
