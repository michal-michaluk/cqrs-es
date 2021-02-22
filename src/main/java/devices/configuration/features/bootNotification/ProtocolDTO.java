package devices.configuration.features.bootNotification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ProtocolDTO {
    String name;
    String version;
    String mediaType;

    @JsonCreator
    public ProtocolDTO(
            @JsonProperty("name") String name,
            @JsonProperty("version") String version,
            @JsonProperty("mediaType") String mediaType) {
        this.name = name;
        this.version = version;
        this.mediaType = mediaType;
    }
}
