package devices.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import devices.configuration.device.events.DeviceAssigned;
import devices.configuration.device.events.LocationSpecified;
import devices.configuration.device.events.OpeningHoursSpecified;
import devices.configuration.device.events.SettingsChanged;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
//        @JsonSubTypes.Type(value = SettingsChangedV1.class, name = "SettingsChanged_v1"),
        @JsonSubTypes.Type(value = SettingsChanged.class, name = "SettingsChanged_v2"),
        @JsonSubTypes.Type(value = DeviceAssigned.class, name = "OwnershipChanged_v1"),
        @JsonSubTypes.Type(value = LocationSpecified.class, name = "LocationChanged_v1"),
        @JsonSubTypes.Type(value = OpeningHoursSpecified.class, name = "OpeningHoursChanged_v1")
})
public interface DomainEvent {
}
