package am.greenbank.repositories.interfaces;

import am.greenbank.entities.image.Image;

import java.util.Optional;

public interface ImageRepository {
    Optional<Image> findImageById(String id);
    Optional<Image> findByName(String id);

    Image saveImage(Image image);

    void deleteById(String id);
}
