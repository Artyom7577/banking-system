package am.greenbank.helpers.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DayOfYearValidationValidator implements ConstraintValidator<DayOfYearValidation, String> {
    private final DateTimeFormatter dateFormatter;

    public DayOfYearValidationValidator(@Qualifier(value = "dateFormatter") DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
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

        String[] split = value.split("-");

        if (!dob.isLeapYear()) {
            if (dob.getMonth() == Month.FEBRUARY && split[0].equals("29")) {
                context.buildConstraintViolationWithTemplate("birth day should be valid");
                return false;
            }
        }

        return true;
    }
}
