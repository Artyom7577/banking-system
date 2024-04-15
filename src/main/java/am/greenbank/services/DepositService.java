package am.greenbank.services;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.deposit.Deposit;
import am.greenbank.entities.deposit.DepositStatus;
import am.greenbank.entities.deposit.DepositType;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.entities.transaction.TransactionType;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.helpers.util.BankUtil;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.DepositRepository;
import am.greenbank.repositories.interfaces.DepositTypeRepository;
import am.greenbank.repositories.interfaces.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {
    private final DepositRepository depositRepository;
    private final DepositTypeRepository depositTypeRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankUtil bankUtil;
    private final NotificationServie notificationServie;

    public Deposit createDeposit(Deposit deposit) {
        String fromAccountNumber = deposit.getFrom();
        Account userAccount = accountRepository.findByAccountNumber(fromAccountNumber).orElseThrow(AccountNotFoundException::new);
        if (userAccount.getCurrency() != deposit.getCurrency()) {
            throw new CurrencyNotMatchException();
        }

        DepositType depositType = depositTypeRepository.findByName(deposit.getDepositName()).orElseThrow(
            () -> new DepositTypeNotFoundException("Deposit type not found")
        );
        boolean optionExists = depositType.getOptions()
            .stream()
            .anyMatch(
                depositTypeOption ->
                    depositTypeOption.getDuration().equals(deposit.getDuration()) &&
                        depositTypeOption.getPercent().equals(deposit.getPercent())
            );
        if (!optionExists) {
            throw new OptionNotFoundException("This option for this deposit type doesn't exist");
        }

        Account bankAccount = bankUtil.getBankAccount(deposit.getCurrency());

        double amount = deposit.getAmount();
        Transaction transaction = Transaction
            .builder()
            .from(
                TransactionEntity
                    .builder()
                    .number(bankAccount.getAccountNumber())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .to(
                TransactionEntity
                    .builder()
                    .number(userAccount.getAccountNumber())
                    .userId(deposit.getUserId())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .amount(amount)
            .description("Creating deposit " + deposit.getDepositName())
            .date(LocalDateTime.now())
            .done(true)
            .currency(deposit.getCurrency())
            .build();

        if (userAccount.getBalance() < amount) {
            throw new TransactionException(transaction, "Insufficient funds");
        }
        userAccount.setBalance(userAccount.getBalance() - amount);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        accountRepository.saveAccount(userAccount);
        accountRepository.saveAccount(bankAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return depositRepository.save(deposit);
    }


    public List<Deposit> getDepositsByUserId(String userId) {
        return depositRepository.findAllByUserId(userId);
    }


    public Deposit getDeposit(String depositId) {
        return depositRepository.findById(depositId).orElseThrow(() -> new DepositNotFoundException("Deposit Not Found"));
    }

    public Deposit updateDeposit(String depositId, Double amount, String from) {
        Deposit deposit = getDeposit(depositId);

        Account userAccount = accountRepository.findByAccountNumber(from).orElseThrow(AccountNotFoundException::new);
        if (userAccount.getCurrency() != deposit.getCurrency()) {
            throw new CurrencyNotMatchException();
        }

        Account bankAccount = bankUtil.getBankAccount(deposit.getCurrency());
        Transaction transaction = Transaction
            .builder()
            .from(
                TransactionEntity
                    .builder()
                    .number(userAccount.getAccountNumber())
                    .userId(deposit.getUserId())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .to(
                TransactionEntity
                    .builder()
                    .number(bankAccount.getAccountNumber())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .amount(amount)
            .description("Adding to deposit " + deposit.getDepositName())
            .date(LocalDateTime.now())
            .done(true)
            .currency(deposit.getCurrency())
            .build();
        if (userAccount.getBalance() < amount) {
            throw new TransactionException(transaction, "Insufficient funds");
        }
        userAccount.setBalance(userAccount.getBalance() - amount);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        deposit.setAmount(deposit.getAmount() + amount);
        accountRepository.saveAccount(userAccount);
        accountRepository.saveAccount(bankAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return depositRepository.save(deposit);
    }

    public Deposit closeDeposit(String depositId) {
        Deposit deposit = getDeposit(depositId);
        Account userAccount = accountRepository.findByAccountNumber(deposit.getFrom()).orElseThrow(AccountNotFoundException::new);
        Account bankAccount = bankUtil.getBankAccount(deposit.getCurrency());
        Double amount = deposit.getAmount();
        Transaction transaction = Transaction
            .builder()
            .from(
                TransactionEntity
                    .builder()
                    .number(bankAccount.getAccountNumber())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .to(
                TransactionEntity
                    .builder()
                    .number(userAccount.getAccountNumber())
                    .userId(deposit.getUserId())
                    .type(TransactionType.ACCOUNT)
                    .build()
            )
            .amount(amount)
            .description("returning to deposit " + deposit.getDepositName())
            .date(LocalDateTime.now())
            .done(true)
            .currency(deposit.getCurrency())
            .build();
        if (bankAccount.getBalance() < amount) {
            throw new TransactionException(transaction, "Transaction can not be done at this moment please try later");
        }

        userAccount.setBalance(userAccount.getBalance() + amount);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        accountRepository.saveAccount(userAccount);
        accountRepository.saveAccount(bankAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);
        deposit.setStatus(DepositStatus.CLOSED);
        return depositRepository.save(deposit);
    }
}
