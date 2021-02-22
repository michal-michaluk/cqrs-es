package devices.configuration.features.images;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedImageTypeException extends RuntimeException {

    public UnsupportedImageTypeException(String allowedTypes) {
        super("Image of unsupported type provided. Allowed types: " + allowedTypes + ".");
    }
}
