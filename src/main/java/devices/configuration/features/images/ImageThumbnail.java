package devices.configuration.features.images;

import lombok.Value;

@Value
class ImageThumbnail {
    String fileName;
    byte[] image;
}
