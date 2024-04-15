package am.greenbank.exceptions.handler;

import am.greenbank.entities.Notification;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.transaction.Transaction;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.CurrencyNotMatchException;
import am.greenbank.exceptions.exceptions.*;
import am.greenbank.helpers.mappers.NotificationMapper;
import am.greenbank.responses.Response;
import am.greenbank.services.NotificationServie;
import am.greenbank.services.TransactionService;
import am.greenbank.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final UserService userService;
    private final TransactionService transactionService;
    private final NotificationServie notificationServie;
    private final NotificationMapper notificationMapper;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Response> handleAlreadyExistsException(AlreadyExistsException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotMatchException.class)
    public ResponseEntity<Response> handleNotMatchException(NotMatchException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        StringBuilder stringBuilder = new StringBuilder("Invalid credentials - ");
        for (var error : fieldErrors) {
            stringBuilder.append(error.getDefaultMessage()).append(System.lineSeparator());
        }
        Response errorResponse = Response.getErrorResponse(stringBuilder.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomMethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleCustomMethodArgumentNotValidException(CustomMethodArgumentNotValidException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response> handleAuthenticationException(AuthenticationException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<Response> handleVerificationException(VerificationException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Response> handleTransactionException(TransactionException exception) {
        Transaction transaction = exception.getTransaction();
        String description = transaction.getDescription() + " - Denied because of " + exception.getLocalizedMessage();
        transaction.setDescription(description);
        transaction.setDone(false);
        transaction.setDate(LocalDateTime.now());
        Account account = transactionService.getAccountFromTransactionEntity(transaction.getFrom());
        User user = userService.getUserByAccount(account);
        transaction.getFrom().setUserId(user.getId());
        Transaction savedTransaction = transactionService.saveTransaction(transaction);
        Notification notification = notificationMapper.getNotificationFromTransactionForSender(savedTransaction);
        notificationServie.sendNotification(notification);
        Response errorResponse = Response.getErrorResponse(savedTransaction, exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordMatchException.class)
    public ResponseEntity<Response> handlePasswordMatchException(PasswordMatchException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotSendException.class)
    public ResponseEntity<Response> handleEmailNotSendException(EmailNotSendException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InUseException.class)
    public ResponseEntity<Response> handleInUseException(InUseException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoanException.class)
    public ResponseEntity<Response> handleLoanException(LoanException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MinimalPaymentAmountException.class)
    public ResponseEntity<Response> handleMinimalPaymentAmountException(MinimalPaymentAmountException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedValueException.class)
    public ResponseEntity<Response> handleUnsupportedValueException(UnsupportedValueException exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleOtherExceptions(Exception exception) {
        Response errorResponse = Response.getErrorResponse(exception.getLocalizedMessage());
        exception.printStackTrace();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}