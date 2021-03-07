package devices.configuration.device.events;

import devices.configuration.DomainEvent;
import lombok.Value;

@Value
public class DeviceOrdered implements DomainEvent {
    String deviceId;
}
