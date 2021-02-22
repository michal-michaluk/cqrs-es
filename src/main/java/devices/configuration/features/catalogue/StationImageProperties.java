package devices.configuration.features.catalogue;

import devices.configuration.features.images.ImageId;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "features.catalogue.default.image")
public class StationImageProperties {

    private String id;

    public ImageId getDefaultImageId() {
        return ImageId.of(id);
    }
}
