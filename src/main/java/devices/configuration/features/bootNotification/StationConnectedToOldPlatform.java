package devices.configuration.features.bootNotification;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public class StationConnectedToOldPlatform implements DomainEvent {
    String stationName;
}
