package devices.configuration.legacy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class StationConnected {

    UUID id;
    Instant occurrenceTime;
    String stationName;
    String xmlRpcVersion;
    String xmlRpcVendor;
    String csms;

    @JsonCreator
    public StationConnected(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurrenceTime") Instant occurrenceTime,
            @JsonProperty("stationName") String stationName,
            @JsonProperty("xmlRpcVersion") String xmlRpcVersion,
            @JsonProperty("xmlRpcVendor") String xmlRpcVendor,
            @JsonProperty("csms") String csms) {
        this.id = id;
        this.occurrenceTime = occurrenceTime;
        this.stationName = stationName;
        this.xmlRpcVersion = xmlRpcVersion;
        this.xmlRpcVendor = xmlRpcVendor;
        this.csms = csms;
    }
}
