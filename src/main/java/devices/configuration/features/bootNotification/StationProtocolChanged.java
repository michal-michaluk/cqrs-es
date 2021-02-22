package devices.configuration.features.bootNotification;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public class StationProtocolChanged implements DomainEvent {
    String stationName;
    String protocolName;
    String protocolVersion;
    String mediaType;
}
