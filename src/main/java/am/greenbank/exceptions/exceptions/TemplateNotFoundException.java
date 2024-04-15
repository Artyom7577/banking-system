package am.greenbank.exceptions.exceptions;

public class TemplateNotFoundException extends NotFoundException {
    public TemplateNotFoundException() {
        super("Template not found");
    }

    public TemplateNotFoundException(String message) {
        super(message);
    }
}
