package devices.configuration.features.bootNotification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BootNotificationFields {
    String softwareVersion;
    String protocolName;
    String protocolVersion;

    @JsonCreator
    public BootNotificationFields(
            @JsonProperty("softwareVersion") String softwareVersion,
            @JsonProperty("protocolName") String protocolName,
            @JsonProperty("protocolVersion") String protocolVersion) {
        this.softwareVersion = softwareVersion;
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
    }
}
