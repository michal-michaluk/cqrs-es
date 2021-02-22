package devices.configuration.features.bootNotification;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public class StationConnectedToCsc implements DomainEvent {
    String stationName;
}
