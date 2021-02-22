package devices.configuration.features.catalogue.fileImport.csc;

import devices.configuration.features.images.ImageId;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Value
public
class UploadStationsToCscCommand {

    MultipartFile stations;
    ImageId imageId;
    MultipartFile image;

    Optional<ImageId> getImageId() {
        return Optional.ofNullable(imageId);
    }

    public Optional<MultipartFile> getImage() {
        return Optional.ofNullable(image);
    }

    public static class Builder {

        private final MultipartFile stations;
        private ImageId imageId;
        private MultipartFile image;

        public Builder(MultipartFile stations) {
            this.stations = requireNonNull(stations);
        }

        public Builder withImageReference(String imageId) {
            this.imageId = ImageId.of(requireNonNull(imageId));
            return this;
        }

        public Builder withImage(MultipartFile image) {
            this.image = Objects.requireNonNull(image);
            return this;
        }

        public UploadStationsToCscCommand build() {
            return new UploadStationsToCscCommand(this.stations, this.imageId, this.image);
        }
    }

}
