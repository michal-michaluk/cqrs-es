package devices.configuration.features.bootNotification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class StationProtocolFromCsc {

    ProtocolDTO protocol;
    String csms;

    @JsonCreator
    public StationProtocolFromCsc(
            @JsonProperty("protocol") ProtocolDTO protocol,
            @JsonProperty("csms") String csms) {
        this.protocol = protocol;
        this.csms = csms;
    }
}
