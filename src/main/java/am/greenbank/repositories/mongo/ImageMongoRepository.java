package am.greenbank.repositories.mongo;

import am.greenbank.entities.image.Image;
import am.greenbank.repositories.interfaces.ImageRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageMongoRepository extends ImageRepository, MongoRepository<Image, String> {

    @Override
    default Optional<Image> findImageById(String id) {
        return findById(id);
    }

    @Override
    default Image saveImage(Image image) {
        return save(image);
    }

    @Override
    default void deleteById(String id) {
        Optional<Image> imageOptional = findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            image.setDeleted(true);
            saveImage(image);
        }
    }
}
