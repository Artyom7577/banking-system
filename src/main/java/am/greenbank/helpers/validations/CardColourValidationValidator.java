package am.greenbank.helpers.validations;

import am.greenbank.entities.cards.CardColour;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardColourValidationValidator implements ConstraintValidator<CardColourValidation, CardColour> {
    private static final String HEX_COLOR_PATTERN = "^#?([A-Fa-f0-9]{3,8})$";
    private static String message;

    @Override
    public void initialize(CardColourValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(CardColour cardColour, ConstraintValidatorContext context) {
        if (cardColour == null) {
            return true; // Null values are considered valid
        }

        boolean isValidFirstHex = isValidHex(cardColour.getFirstHex(), "firstHex", context);
        boolean isValidSecondHex = isValidHex(cardColour.getSecondHex(), "secondHex", context);

        return isValidFirstHex && isValidSecondHex;
    }

    private boolean isValidHex(String hex, String propertyName, ConstraintValidatorContext context) {
        if (hex == null || !hex.matches(HEX_COLOR_PATTERN)) {
            context
                .buildConstraintViolationWithTemplate(message + "in " + propertyName);
            return false;
        }
        return true;
    }
}
