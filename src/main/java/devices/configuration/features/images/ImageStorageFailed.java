package devices.configuration.features.images;

import java.io.IOException;

public class ImageStorageFailed extends RuntimeException {

    public ImageStorageFailed(String fileName, IOException ioException) {
        super("Cannot save file " + fileName + ".", ioException);
    }
}
