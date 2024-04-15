package am.greenbank.helpers.mappers;

import am.greenbank.dtos.ImageDto;
import am.greenbank.entities.image.Image;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class ImageMapper {
    public ImageDto mapImageToImageDto(Image image) {
        return ImageDto
            .builder()
            .id(image.getId())
            .name(image.getName())
            .type(image.getType())
            .imageData(image.getImageData())
            .build();
    }
}
