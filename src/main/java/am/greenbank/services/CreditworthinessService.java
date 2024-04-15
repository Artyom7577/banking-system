package am.greenbank.services;

import am.greenbank.entities.Creditworthiness;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.CreditworthinessInUseException;
import am.greenbank.exceptions.exceptions.CreditworthinessNotFoundException;
import am.greenbank.exceptions.exceptions.CreditworthinessWithNameAlreadyExistsException;
import am.greenbank.repositories.interfaces.CreditworthinessRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditworthinessService {

    private final CreditworthinessRepository creditworthinessRepository;
    private final UserRepository userRepository;

    public Creditworthiness addCreditworthiness(Creditworthiness creditworthiness) {
        validateCreditworthinessNameNotExist(creditworthiness.getName());
        List<Creditworthiness> creditworthinesses = getAllCreditworthinesses();
        adjustOrderForNewCreditworthiness(creditworthiness, creditworthinesses);
        creditworthinessRepository.saveAllCreditworthinesses(creditworthinesses);
        return creditworthinessRepository.save(creditworthiness);
    }

    private void adjustOrderForNewCreditworthiness(Creditworthiness newCreditworthiness, List<Creditworthiness> existingCreditworthinesses) {
        int newOrder = newCreditworthiness.getOrder();

        if (newOrder < existingCreditworthinesses.size()) {
            for (int i = newOrder; i < existingCreditworthinesses.size(); i++) {
                existingCreditworthinesses.get(i).setOrder(existingCreditworthinesses.get(i).getOrder() + 1);
            }
        } else {
            newCreditworthiness.setOrder(existingCreditworthinesses.size());
        }
    }

    public List<Creditworthiness> getAllCreditworthinesses() {
        Sort sort = Sort.by(Sort.Direction.ASC, "order");
        return creditworthinessRepository.findAll(sort);
    }

    public Creditworthiness getCreditworthinessById(String creditworthinessId) {
        return creditworthinessRepository.findById(creditworthinessId)
            .orElseThrow(() -> new CreditworthinessNotFoundException("Creditworthiness by provided id not found"));
    }

    public Creditworthiness updateCreditworthiness(String creditworthinessId, Creditworthiness updatedCreditworthiness) {
        Creditworthiness creditworthiness = getCreditworthinessById(creditworthinessId);
        updateFieldsIfProvided(creditworthiness, updatedCreditworthiness);
        updateOrderIfProvided(creditworthiness, updatedCreditworthiness.getOrder());
        creditworthinessRepository.save(creditworthiness);
        return creditworthiness;
    }

    private void updateFieldsIfProvided(Creditworthiness creditworthiness, Creditworthiness updatedCreditworthiness) {
        updateNameIfProvided(creditworthiness, updatedCreditworthiness.getName());
        updateUnblockDurationIfProvided(creditworthiness, updatedCreditworthiness.getUnblockDuration());
        updateCanGetLoanIfProvided(creditworthiness, updatedCreditworthiness.isCanGetLoan());
    }

    private void updateNameIfProvided(Creditworthiness creditworthiness, String name) {
        if (name != null && !name.isEmpty()) {
            validateUniqueCreditworthinessName(name, creditworthiness);
            creditworthiness.setName(name);
        }
    }

    private void updateUnblockDurationIfProvided(Creditworthiness creditworthiness, Integer unblockDuration) {
        if (unblockDuration != null) {
            creditworthiness.setUnblockDuration(unblockDuration);
        }
    }

    private void updateCanGetLoanIfProvided(Creditworthiness creditworthiness, Boolean canGetLoan) {
        if (canGetLoan != null) {
            creditworthiness.setCanGetLoan(canGetLoan);
        }
    }

    private void updateOrderIfProvided(Creditworthiness creditworthiness, Integer order) {
        if (order != null) {
            List<Creditworthiness> creditworthinesses = getAllCreditworthinesses();
            if (order > creditworthinesses.size()) {
                order = creditworthinesses.size();
            }

            adjustOrderForUpdatedCreditworthiness(creditworthiness, creditworthinesses, order);
        }
    }

    private void adjustOrderForUpdatedCreditworthiness(Creditworthiness updatedCreditworthiness, List<Creditworthiness> existingCreditworthinesses, int newOrder) {
        int oldOrder = updatedCreditworthiness.getOrder();

        if (oldOrder < newOrder) {
            for (int i = oldOrder; i < newOrder; i++) {
                existingCreditworthinesses.get(i).setOrder(existingCreditworthinesses.get(i).getOrder() - 1);
            }
        } else if (oldOrder > newOrder) {
            for (int i = oldOrder - 1; i >= newOrder; i--) {
                existingCreditworthinesses.get(i).setOrder(existingCreditworthinesses.get(i).getOrder() + 1);
            }
        }

        updatedCreditworthiness.setOrder(newOrder);
    }

    private void validateCreditworthinessNameNotExist(String name) {
        creditworthinessRepository.findByName(name)
            .ifPresent(cred -> {
                throw new CreditworthinessWithNameAlreadyExistsException("Creditworthiness with name already exists");
            });
    }

    private void validateUniqueCreditworthinessName(String name, Creditworthiness currentCreditworthiness) {
        creditworthinessRepository.findByName(name)
            .ifPresent(cred -> {
                if (!cred.getId().equals(currentCreditworthiness.getId())) {
                    throw new CreditworthinessWithNameAlreadyExistsException("Creditworthiness with name already exists");
                }
            });
    }

    public void deleteCreditworthiness(String creditworthinessId) {
        Creditworthiness creditworthiness = creditworthinessRepository.findById(creditworthinessId)
            .orElseThrow(() -> new CreditworthinessNotFoundException("Creditworthiness by provided id not found"));
        List<User> users = userRepository.findByCreditworthinessId(creditworthinessId);
        if (!users.isEmpty()) {
            throw new CreditworthinessInUseException("Creditworthiness is in use");
        }
        List<Creditworthiness> remainingCreditworthinesses = getAllCreditworthinesses();
        int removedOrder = creditworthiness.getOrder();
        for (int i = removedOrder; i < remainingCreditworthinesses.size(); i++) {
            Creditworthiness remainingCreditworthiness = remainingCreditworthinesses.get(i);
            remainingCreditworthiness.setOrder(remainingCreditworthiness.getOrder() - 1);
        }
        creditworthinessRepository.saveAllCreditworthinesses(remainingCreditworthinesses);
        creditworthinessRepository.deleteById(creditworthinessId);
    }

}
