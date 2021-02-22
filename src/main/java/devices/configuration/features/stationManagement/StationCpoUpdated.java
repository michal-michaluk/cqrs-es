package devices.configuration.features.stationManagement;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public class StationCpoUpdated implements DomainEvent {
    String TYPE = "CpoUpdated";
    String cpoName;
}
