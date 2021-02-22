package devices.configuration.features.images;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class CannotCreateThumbnailFromImage extends RuntimeException {

    public CannotCreateThumbnailFromImage(IOException ioException) {
        super(ioException);
    }
}
