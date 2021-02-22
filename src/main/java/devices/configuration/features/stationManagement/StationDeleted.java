package devices.configuration.features.stationManagement;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public
class StationDeleted implements DomainEvent {
    String TYPE = "StationCreated";
    String stationName;
}
