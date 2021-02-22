package devices.configuration.features.catalogue.photo;

import lombok.Builder;
import lombok.Value;

import java.net.URL;
import java.util.UUID;

@Value
@Builder
public class PhotoResponse {
    private UUID id;
    private URL url;
    private String name;
    private String category;
}
