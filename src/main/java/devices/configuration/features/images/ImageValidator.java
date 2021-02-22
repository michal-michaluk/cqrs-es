package devices.configuration.features.images;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static java.util.Objects.requireNonNull;

class ImageValidator {

    static void validate(MultipartFile image) {
        requireNonNull(image.getOriginalFilename());

        Set allowedTypes = Set.of(
                MediaType.IMAGE_JPEG_VALUE, // includes .jpg and .jpeg
                MediaType.IMAGE_PNG_VALUE);

        if (!allowedTypes.contains(image.getContentType())) {
            throw new UnsupportedImageTypeException(allowedTypes.toString());
        }
    }
}
