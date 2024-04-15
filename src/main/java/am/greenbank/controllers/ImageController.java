package am.greenbank.controllers;

import am.greenbank.services.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image", description = "Image Description")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageDataService;



//    @GetMapping("/{name}")
//    public ResponseEntity<?> getImageByName(@PathVariable("name") String name){
//        byte[] image = imageDataService.getImage(name);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
//                .body(image);
//    }

//    private MediaType determineMediaType(String fileExtension) {
//        return switch (fileExtension.toLowerCase()) {
//            case "png" -> MediaType.IMAGE_PNG;
//            case "jpeg" -> MediaType.IMAGE_JPEG;
//            default -> MediaType.APPLICATION_OCTET_STREAM;
//        };
//    }
}
