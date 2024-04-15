package am.greenbank.services;

import am.greenbank.entities.account.Account;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.transaction.TransactionEntity;
import am.greenbank.entities.transaction.TransactionType;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.CardRepository;
import am.greenbank.repositories.interfaces.TransactionRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import am.greenbank.requests.QRType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MongoTemplate mongoTemplate;

    public Transaction createTransaction(Transaction transaction) {
        Account from = getAccountFromTransactionEntity(transaction.getFrom());
        Account to;
        transaction.setCurrency(from.getCurrency());
        try {
            to = getAccountFromTransactionEntity(transaction.getTo());
        } catch (JwtException exception) {
            String msg;
            if (exception instanceof ExpiredJwtException) {
                msg = "QR expired";
            } else {
                msg = "Incorrect QR Data";
            }

            throw new TransactionException(transaction, msg);
        } catch (NotFoundException exception) {
            throw new TransactionException(transaction, exception.getLocalizedMessage());
        }
        validateCurrency(transaction, from, to);
        Double amount = transaction.getAmount();
        validateAmount(transaction, from, amount);
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        transaction.setDate(LocalDateTime.now());
//        transaction.setDate(LocalDateTime.now());
        transaction.setDone(true);
        User userFrom = userRepository.findByAccountId(from.getId())
            .orElseThrow(UserNotFoundException::new);
        User userTo = userRepository.findByAccountId(to.getId())
            .orElseThrow(UserNotFoundException::new);
        accountRepository.saveAccount(from);
        accountRepository.saveAccount(to);
        transaction.getFrom().setUserId(userFrom.getId());
        transaction.getTo().setUserId(userTo.getId());
        return transactionRepository.save(transaction);
    }

    private void validateAmount(Transaction transaction, Account from, Double amount) {
        if (from.getBalance() < amount) {
            throw new TransactionException(transaction, "Insufficient funds");
        }
    }

    private void validateCurrency(Transaction transaction, Account from, Account to) {
        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new TransactionException(transaction, "Incomplete currencies. Can't complete transaction from currency"
                + from.getCurrency() + " to currency" + to.getCurrency());
        }
    }

    public Account getAccountFromTransactionEntity(TransactionEntity transactionEntity) {
        return switch (transactionEntity.getType()) {
            case ACCOUNT -> getAccountByNumber(transactionEntity.getNumber());
            case CARD -> getAccountByCardNumber(transactionEntity.getNumber());
            case PHONE -> updateTransactionEntityAndGetAccountByPhone(transactionEntity);
            case QR_ACCOUNT, QR_CARD -> updateTransactionEntityAndGetAccountByQRData(transactionEntity);
        };
    }

    private Account updateTransactionEntityAndGetAccountByPhone(TransactionEntity transactionEntity) {
        User user = userRepository.findByPhone(transactionEntity.getNumber())
            .orElseThrow(() -> new UserNotFoundException("User with phone number: "
                + transactionEntity.getNumber() + " not found"));


        Account account = user
            .getAccounts()
            .stream()
            .filter(Account::getIsDefault)
            .findAny()
            .orElseThrow(() -> new AccountNotFoundException("Default account for user with phone: " + transactionEntity.getNumber() + " not fount"));

        transactionEntity.setNumber(account.getAccountNumber());

        return account;
    }

    private Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException("Account: " + accountNumber + " not found"));
    }

    private Account getAccountByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
            .map(Card::getAccount)
            .orElseThrow(() -> new CardNotFoundException("Card: " + cardNumber + " not found"));
    }

    private Account updateTransactionEntityAndGetAccountByQRData(TransactionEntity transactionEntity) {
        String number = jwtService.extractClaim(transactionEntity.getNumber(), claims -> claims.get("number", String.class));
        QRType type = QRType.valueOf(
            jwtService.extractClaim(
                transactionEntity.getNumber(), claims -> claims.get("type", String.class)
            )
        );

        if (type.equals(QRType.ACCOUNT)) {
            transactionEntity.setType(TransactionType.ACCOUNT);
            return getAccountByNumber(number);
        }

        transactionEntity.setType(TransactionType.CARD);
        return getAccountByCardNumber(number);
    }


    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Page<Transaction> findTransactionsByCustomFilter(
        LocalDateTime dateFrom, LocalDateTime dateTo, String accountNumber, String cardNumber, String userId,
        Double amountMin, Double amountMax, Boolean isCredit, Boolean isDone, String description, Pageable pageable
    ) {
        Criteria criteria = new Criteria();

        if (isCredit == null) {
            isCreditNull(accountNumber, cardNumber, userId, criteria);
        } else if (isCredit) {
            isCreditTrue(accountNumber, cardNumber, userId, criteria);
        } else {
            isCreditFalse(accountNumber, cardNumber, userId, criteria);
            isDone = true;
        }

        Optional.ofNullable(dateFrom).ifPresent(df -> criteria.and("date").gte(df));
        Optional.ofNullable(dateTo).ifPresent(dt -> criteria.and("date").lte(dt));
        Optional.ofNullable(amountMin).ifPresent(am -> criteria.and("amount").gte(am));
        Optional.ofNullable(amountMax).ifPresent(am -> criteria.and("amount").lte(am));
        Optional.ofNullable(isDone).ifPresent(done -> criteria.and("done").is(done));
        Optional.ofNullable(description).ifPresent(desc -> criteria.and("description").is(desc));


        Query query = new Query(criteria)
            .with(pageable)
            .with(Sort.by(Sort.Direction.DESC, "date"));

        List<Transaction> transactions = mongoTemplate.find(query, Transaction.class);

        Query countQuery = new Query(criteria);
        long count = mongoTemplate.count(countQuery, Transaction.class);

        return PageableExecutionUtils.getPage(transactions, pageable, () -> count);
    }

    private void isCreditNull(String accountNumber, String cardNumber, String userId, Criteria criteria) {
        if (accountNumber != null) {
//                criteria.orOperator(
//                    Criteria
//                        .where("from.number")
//                        .regex(accountNumber)
//                        .and("from.type")
//                        .is(TransactionType.ACCOUNT),
//                    Criteria
//                        .where("to.number")
//                        .regex(accountNumber)
//                );
            criteriaForIsCreditNullAndAccountNumberNotNull(accountNumber, criteria);
        } else if (cardNumber != null) {
//                criteria.orOperator(
//                    Criteria
//                        .where("from.number")
//                        .regex(cardNumber)
//                        .and("from.type")
//                        .is(TransactionType.CARD),
//
//                    Criteria
//                        .where("to.number")
//                        .regex(cardNumber)
//                );

            criteriaForIsCreditNullAndCardNumberNotNull(cardNumber, criteria);
        } else if (userId != null) {
//                criteria.andOperator(
//                    Criteria
//                        .where("from.userId")
//                        .is(userId),
//                    Criteria
//                        .where("to.userId")
//                        .is(userId)
//                );
            criteriaForIsCreditNullAndUserId(userId, criteria);
        } else {
            throw new RuntimeException("no supported value for  this search");
        }
    }

    private void isCreditTrue(String accountNumber, String cardNumber, String userId, Criteria criteria) {
        if (accountNumber != null) {
            criteriaForIsCreditTrueAndCardNumberNotNull(accountNumber, TransactionType.ACCOUNT, criteria);
        } else if (cardNumber != null) {
            criteriaForIsCreditTrueAndCardNumberNotNull(cardNumber, TransactionType.CARD, criteria);
        } else if (userId != null) {
            criteriaForIsCreditTrueAndUserId(userId, criteria);
        } else {
            throw new RuntimeException("no supported value for  this search");
        }
    }

    private void isCreditFalse(String accountNumber, String cardNumber, String userId, Criteria criteria) {
        if (accountNumber != null) {
            criteriaForIsCreditFalseAndAccountNumberOrCardNumberNotNull(accountNumber, criteria);
        } else if (cardNumber != null) {
            criteriaForIsCreditFalseAndAccountNumberOrCardNumberNotNull(cardNumber, criteria);
        } else if (userId != null) {
            criteriaForIsCreditFalseAndUserId(userId, criteria);
        } else {
            throw new RuntimeException("no supported value for  this search");
        }
    }

    private void criteriaForIsCreditFalseAndUserId(String userId, Criteria criteria) {
        criteria
            .and("to.userId")
            .is(userId);
    }

    private void criteriaForIsCreditFalseAndAccountNumberOrCardNumberNotNull(String number, Criteria criteria) {
        criteria
            .and("to.number")
            .is(number);
    }

    private void criteriaForIsCreditTrueAndUserId(String userId, Criteria criteria) {
        criteria
            .and("from.userId").is(userId);
    }

    private void criteriaForIsCreditTrueAndCardNumberNotNull(String number, TransactionType type, Criteria criteria) {
        criteria
            .and("from.number").is(number)
            .and("from.type").is(type);
    }

//    private void criteriaForIsCreditTrueAndCardNumberNotNull1(Acc acc, Criteria criteria) {
//        criteria
//            .and("from.number").is(acc.number)
//            .and("from.type").is(acc.type);
//    }

    private void criteriaForIsCreditNullAndUserId(String userId, Criteria criteria) {
        Criteria or = new Criteria().orOperator(
            Criteria.where("from.userId").is(userId),
            Criteria.where("to.userId").is(userId)
        );
        criteria.andOperator(or);
    }

    private void criteriaForIsCreditNullAndCardNumberNotNull(String cardNumber, Criteria criteria) {
        Criteria or = new Criteria().orOperator(
            Criteria.where("from.number").is(cardNumber)
                .and("from.type").is(TransactionType.CARD),
            Criteria.where("to.number").is(cardNumber)
                .and("from.type").in(TransactionType.CARD, TransactionType.QR_CARD)
        );

        criteria.andOperator(or);
    }

    private void criteriaForIsCreditNullAndAccountNumberNotNull(String accountNumber, Criteria criteria) {
        Criteria or = new Criteria().orOperator(
            Criteria.where("from.number").is(accountNumber)
                .and("from.type").is(TransactionType.CARD),
            Criteria.where("to.number").is(accountNumber)
                .and("from.type").in(TransactionType.ACCOUNT, TransactionType.PHONE, TransactionType.QR_ACCOUNT)
        );

        criteria.andOperator(or);
    }

//    private void criteriaForIsCreditNullAndAccountNumberNotNull1(String accountNumber, Criteria criteria) {
//        criteriaForIsCreditTrueAndCardNumberNotNull(accountNumber, TransactionType.ACCOUNT, criteria);
//        criteriaForIsCreditFalseAndAccountNumberOrCardNumberNotNull(accountNumber, criteria);
//    }
}