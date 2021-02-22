package devices.configuration.features.stationManagement;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public class StationCreated implements DomainEvent {
    String TYPE = "StationCreated";
    String stationName;
}
