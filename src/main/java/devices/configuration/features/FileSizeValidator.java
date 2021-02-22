package devices.configuration.features;

import devices.configuration.features.catalogue.exceptions.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@RequiredArgsConstructor
public class FileSizeValidator {

    private final int sizeLimitInMb;

    public Optional<ErrorMessage> validate(MultipartFile image) {
        if (image.getSize() > toBytes(sizeLimitInMb)) {
            return of(new ErrorMessage("Image " + image.getName() + " exceeded maximum image size which is " + sizeLimitInMb + " MB"));
        }

        return empty();
    }

    private long toBytes(int megabytes) {
        return megabytes * 1024 * 1024;
    }
}
