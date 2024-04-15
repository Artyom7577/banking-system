package am.greenbank.helpers.mappers;

import am.greenbank.dtos.TransactionDto;
import am.greenbank.dtos.TransactionEntityDto;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.entities.transaction.TransactionType;
import am.greenbank.exceptions.exceptions.AccountNotFoundException;
import am.greenbank.exceptions.exceptions.CardNotFoundException;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.CardRepository;
import am.greenbank.requests.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public Transaction mapTransactionDtoToTransaction(TransactionDto transactionDto) {
        return Transaction
            .builder()
            .id(transactionDto.getId())
            .from(
                getTransactionEntity(transactionDto.getFrom())
            )
            .to(
                getTransactionEntity(transactionDto.getTo())
            )
            .amount(transactionDto.getAmount())
            .description(transactionDto.getDescription())
            .date(LocalDateTime.parse(transactionDto.getDate(), dateTimeFormatter))
            .done(transactionDto.getDone())
            .build();
    }

    private TransactionEntity getTransactionEntity(TransactionEntityDto transactionEntityDto) {
        return TransactionEntity
            .builder()
            .number(transactionEntityDto.getNumber())
            .type(transactionEntityDto.getType())
            .build();
    }

    public TransactionDto mapTransactionToTransactionDto(Transaction transaction) {
        return TransactionDto
            .builder()
            .id(transaction.getId())
            .from(getTransactionEntityDto(transaction.getFrom()))
            .to(getTransactionEntityDto(transaction.getTo()))
            .amount(transaction.getAmount())
            .description(transaction.getDescription())
            .date(transaction.getDate().format(dateTimeFormatter))
            .done(transaction.getDone())
            .build();
    }

    private TransactionEntityDto getTransactionEntityDto(TransactionEntity transactionEntity) {
        return new TransactionEntityDto(transactionEntity.getNumber(), transactionEntity.getType());
    }

    private Account getAccount(String accountNumber) {
        return accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(AccountNotFoundException::new);
    }

    public Transaction mapTransactionRequestToTransaction(TransactionRequest transactionRequest) {
        return Transaction
            .builder()
            .from(getTransactionEntityFrom(transactionRequest.getFrom(), transactionRequest.getType()))
            .to(TransactionEntity
                .builder()
                .number(transactionRequest.getTo())
                .type(transactionRequest.getType())
                .build()
            )
            .amount(transactionRequest.getAmount())
            .description(transactionRequest.getDescription())
            .build();
    }

    private TransactionEntity getTransactionEntityFrom(String number, TransactionType type) {
        return switch (type) {
            case ACCOUNT, PHONE, QR_ACCOUNT -> TransactionEntity
                .builder()
                .number(number)
                .type(TransactionType.ACCOUNT)
                .build();
            case CARD, QR_CARD -> TransactionEntity
                .builder()
                .number(number)
                .type(TransactionType.CARD)
                .build();
        };
    }

    private Card getCard(String cardNumber) {
        return cardRepository
            .findByCardNumber(cardNumber)
            .orElseThrow(CardNotFoundException::new);
    }
}

