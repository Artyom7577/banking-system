package am.greenbank.exceptions.exceptions;

public class ImageNotFoundException extends NotFoundException {
    public ImageNotFoundException() {
        super("Image not found");
    }
}
