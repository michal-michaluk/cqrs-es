package devices.configuration.features.images;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CouldNotFindImageWithGivenId extends RuntimeException {

    public CouldNotFindImageWithGivenId(ImageId imageId) {
        super("Could not find image with given id " + imageId.getId() + ".");
    }
}
