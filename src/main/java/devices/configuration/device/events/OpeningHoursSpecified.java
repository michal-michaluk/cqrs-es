package devices.configuration.device.events;

import devices.configuration.DomainEvent;
import devices.configuration.device.OpeningHours;
import lombok.Value;

@Value
public class OpeningHoursSpecified implements DomainEvent {
    String deviceId;
    OpeningHours openingHours;
}
