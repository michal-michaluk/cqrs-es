package devices.configuration.legacy;

import devices.configuration.DomainEvent;
import lombok.Value;

/**
 * Outgoing event for other systems.
 **/
@Value
public class StationLocationUpdated implements DomainEvent {
    String stationName;
    StationLocation location;
    boolean update;

}
