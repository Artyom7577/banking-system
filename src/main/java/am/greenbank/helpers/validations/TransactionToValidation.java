package am.greenbank.helpers.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;


@Constraint(validatedBy = {TransactionToValidationValidator.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionToValidation {
    String message() default "${am.greenbank.helpers.validations.TransactionToValidation.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        TransactionToValidation[] value();
    }
}