package devices.configuration.features.bootNotification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class StationProtocolDTO {

    ProtocolDTO protocol;
    String csms;
    String stationName;

    @JsonCreator
    public StationProtocolDTO(
            @JsonProperty("protocol") ProtocolDTO protocol,
            @JsonProperty("csms") String csms,
            @JsonProperty("stationName") String stationName) {
        this.protocol = protocol;
        this.csms = csms;
        this.stationName = stationName;
    }
}
