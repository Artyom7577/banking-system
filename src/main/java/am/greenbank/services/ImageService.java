package am.greenbank.services;

import am.greenbank.entities.image.Image;
import am.greenbank.entities.user.User;
import am.greenbank.exceptions.exceptions.ImageNotFoundException;
import am.greenbank.exceptions.exceptions.UserNotFoundException;
import am.greenbank.helpers.util.ImageUtil;
import am.greenbank.repositories.interfaces.ImageRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public String uploadImage(MultipartFile file, String userId) throws IOException {
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);

        if (user.getImg() != null) {
            String img = user.getImg();
            imageRepository.deleteById(img);
        }

        Image image = imageRepository.saveImage(Image
            .builder()
            .name(file.getOriginalFilename())
            .type(file.getContentType())
            .imageData(ImageUtil.compressImage(file.getBytes())).build());

        return image.getId();
    }


    public Image getInfoByImageByName(String name) {

        Optional<Image> dbImage = imageRepository.findByName(name);

        return Image.builder()
            .name(dbImage.get().getName())
            .type(dbImage.get().getType())
            .imageData(ImageUtil.decompressImage(dbImage.get().getImageData())).build();
    }

    public Image getImageById(String id) {
        Image imageData = imageRepository.findImageById(id).orElseThrow(ImageNotFoundException::new);

        return Image.builder()
            .id(imageData.getId())
            .name(imageData.getName())
            .type(imageData.getType())
            .imageData(ImageUtil.decompressImage(imageData.getImageData()))
            .build();
    }

    public byte[] getImage(String name) {
        Optional<Image> dbImage = imageRepository.findByName(name);
        byte[] image = ImageUtil.decompressImage(dbImage.get().getImageData());
        return image;
    }

}
