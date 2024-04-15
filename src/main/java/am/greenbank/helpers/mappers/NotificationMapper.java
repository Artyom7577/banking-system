package am.greenbank.helpers.mappers;

import am.greenbank.dtos.NotificationDto;
import am.greenbank.entities.Notification;
import am.greenbank.entities.account.Currency;
import am.greenbank.entities.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final DateTimeFormatter dateTimeFormatter;

    public NotificationDto mapNotificationToNotificationDto(Notification notification) {
        return NotificationDto
            .builder()
            .id(notification.getId())
            .userId(notification.getUserId())
            .message(notification.getMessage())
            .time(notification.getTime().format(dateTimeFormatter))
            .read(notification.isRead())
            .build();
    }

    public Notification getNotificationFromTransactionForSender(Transaction transaction) {
        String message = formatTransactionMessage(
            "sent", transaction
        );

        return Notification.builder()
            .userId(transaction.getFrom().getUserId())
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }

    public Notification getNotificationFromTransactionForReceiver(Transaction transaction) {
        String message = formatTransactionMessage(
            "received", transaction
        );

        return Notification.builder()
            .userId(transaction.getFrom().getUserId())
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }

    private String formatTransactionMessage(String action, Transaction transaction) {
        return String.format(
            "Transaction %s to %s %s from %s %s with amount %s %s and description %s at %s ",
            action,
            transaction.getTo().getType(),
            transaction.getTo().getNumber(),
            transaction.getFrom().getType(),
            transaction.getFrom().getNumber(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getDescription(),
            dateTimeFormatter.format(transaction.getDate())
        );
    }

    private String formatDepositMessage(String type, String name, String action, Double amount, Currency currency, String direction, String account) {
        return type + " " + name + " " + action + " with amount " + amount + currency + " " + direction + " account" + account;
    }

    public Notification getNotification(String type, String userId, String name, String action, Double amount, Currency currency, String account) {
        String message = formatDepositMessage(type, name, action, amount, currency, "from", account);
        return Notification.builder()
            .userId(userId)
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }

    public Notification getDepositCreationNotification(String userId, String depositName, Double amount, Currency currency, String account) {
        String message = formatDepositMessage("Deposit", depositName, "created", amount, currency, "from", account);
        return Notification.builder()
            .userId(userId)
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }

    public Notification getDepositClosingNotification(String userId, String depositName, Double amount, Currency currency, String account) {
        String message = formatClosingDepositMessage("Deposit", depositName, amount, currency, account);
        return Notification.builder()
            .userId(userId)
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }

    private String formatClosingDepositMessage(String type, String depositName, Double amount, Currency currency, String account) {
        return type + depositName + " " + " closed and amount " + amount + currency + "was sent to account" + account;
    }

    public Notification getLoanCreationNotification(String userId, String loanName, Double amount, Currency currency, String account) {
        String message = formatDepositMessage("Loan", loanName, "created", amount, currency, "to", account);
        return Notification.builder()
            .userId(userId)
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }
}
