package am.greenbank.helpers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserAgeValidationValidator implements ConstraintValidator<UserAgeValidation, String> {
    private final DateTimeFormatter dateFormatter;
    private int min;
    private int max;
    private String message;

    public UserAgeValidationValidator(@Qualifier(value = "dateFormatter") DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void initialize(UserAgeValidation constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        LocalDate dob;
        try {
            dob = LocalDate.parse(value, dateFormatter);
        } catch (DateTimeParseException e) {
            context.buildConstraintViolationWithTemplate("birth day should be valid");
            return false;
        }

        LocalDate currenDate = LocalDate.now();
        int age = Period.between(dob, currenDate).getYears();
        if (age < min || age > max) {
            context.buildConstraintViolationWithTemplate(message);
            return false;
        }
        return true;
    }
}
