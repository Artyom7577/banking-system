package am.greenbank.services;

import am.greenbank.entities.Option;
import am.greenbank.entities.deposit.Deposit;
import am.greenbank.entities.deposit.DepositType;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.repositories.interfaces.DepositRepository;
import am.greenbank.repositories.interfaces.DepositTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositTypeService {
    private final DepositTypeRepository depositTypeRepository;
    private final DepositRepository depositRepository;

    public DepositType createDepositType(DepositType depositType) {
        if (depositTypeRepository.findByName(depositType.getName()).isPresent()) {
            throw new DepositTypeAlreadyExistsException("Deposit type with this name already exists");
        }
        return depositTypeRepository.save(depositType);
    }

    public List<DepositType> getAll() {
        return depositTypeRepository.findAll();
    }

    public void deleteByName(String depositName) {
        depositTypeRepository.findByName(depositName)
            .orElseThrow(() -> new DepositTypeNotFoundException("Deposit type with name provided name not found"));
        List<Deposit> allByDepositName = depositRepository.findAllByDepositName(depositName);
        if (!allByDepositName.isEmpty()) {
            throw new DepositTypeInUseException("Deposit type is in use");
        }
        depositTypeRepository.deleteByName(depositName);
    }

    public DepositType addOptionToDepositType(String depositName, Option option) {
        DepositType depositType = depositTypeRepository.findByName(depositName).orElseThrow(
            () -> new DepositTypeNotFoundException("Deposit type with name provided name not found")
        );
        List<Option> options = depositType.getOptions();
        if (options.contains(option)) {
            throw new OptionAlreadyExistsException("This option for this deposit type already exists");
        }
        options.add(option);
        return depositTypeRepository.save(depositType);
    }

    public DepositType removeOptionToDepositType(String depositName, Option option) {
        DepositType depositType = depositTypeRepository.findByName(depositName).orElseThrow(
            () -> new DepositTypeNotFoundException("Deposit type with name provided name not found")
        );
        List<Option> options = depositType.getOptions();
        if (!options.contains(option)) {
            throw new OptionNotFoundException("This option for this deposit type doesn't exist");
        }
        options.remove(option);
        return depositTypeRepository.save(depositType);
    }

    //    @Transactional
    public DepositType updateDepositType(String depositName, String newDepositName, Boolean available) {
        DepositType depositType = depositTypeRepository.findByName(depositName).orElseThrow(
            () -> new DepositTypeNotFoundException("Deposit type with name provided name not found")
        );
        if (depositTypeRepository.findByName(newDepositName).isPresent()) {
            throw new DepositTypeAlreadyExistsException("Deposit type with this name already exists");
        }
        if (newDepositName != null && !newDepositName.equals(depositName)) {
            List<Deposit> allByDepositName = depositRepository.findAllByDepositName(depositName);
            allByDepositName.forEach(deposit -> deposit.setDepositName(newDepositName));
            depositRepository.saveAllDeposits(allByDepositName);
            depositType.setName(newDepositName);
        }
        if (available != null) {
            depositType.setAvailable(available);
        }
        return depositTypeRepository.save(depositType);
    }
}
