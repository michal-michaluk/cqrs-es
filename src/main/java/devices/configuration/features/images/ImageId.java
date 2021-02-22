package devices.configuration.features.images;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageId {

    public static ImageId of(String id) {
        requireNonNull(id, "Image id cannot be null!");
        checkArgument(!id.isBlank(), "Image id cannot be empty!");

        return new ImageId(id);
    }

    String id;
}
