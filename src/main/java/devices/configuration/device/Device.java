package devices.configuration.device;

import devices.configuration.DomainEvent;
import devices.configuration.device.events.DeviceAssigned;
import devices.configuration.device.events.LocationSpecified;
import devices.configuration.device.events.OpeningHoursSpecified;
import devices.configuration.device.events.SettingsChanged;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class Device {

    final String deviceId;
    final List<DomainEvent> events;

    private Ownership ownership;
    private Location location;
    private OpeningHours openingHours;
    private Settings settings;

    public void assignTo(Ownership ownership) {
        if (!Objects.equals(this.ownership, ownership)) {
            this.ownership = ownership;
            return events.add(new DeviceAssigned(deviceId, ownership));
        }
    }

    public void updateLocation(Location location) {
        if (!Objects.equals(this.location, location)) {
            this.location = location;
            events.add(new LocationSpecified(deviceId, location));
        }
    }

    public void updateOpeningHours(OpeningHours openingHours) {
        if (!Objects.equals(this.openingHours, openingHours)) {
            this.openingHours = openingHours;
            events.add(new OpeningHoursSpecified(deviceId, openingHours));
        }
    }

    public void updateSettings(Settings settings) {
        Settings newSettings = this.settings.merge(settings);
        if (!Objects.equals(this.settings, newSettings)) {
            this.settings = newSettings;
            events.add(new SettingsChanged(deviceId, settings));
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<DomainEvent> getEvents() {
        return events;
    }

    private Violations checkViolations() {
        return Violations.builder()
                .operatorNotAssigned(ownership == null || ownership.getOperator() == null)
                .providerNotAssigned(ownership == null || ownership.getProvider() == null)
                .locationMissing(location == null)
                .showOnMapButMissingLocation(settings.isShowOnMap() && location == null)
                .showOnMapButNoPublicAccess(settings.isShowOnMap() && !settings.isPublicAccess())
                .build();
    }

    public DeviceSnapshot toSnapshot() {
        Violations violations = checkViolations();
        Visibility visibility = Visibility.basedOn(
                violations.isValid() && settings.isPublicAccess(),
                settings.isShowOnMap()
        );
        return new DeviceSnapshot(
                deviceId,
                ownership,
                location,
                openingHours,
                settings,
                violations,
                visibility
        );
    }
}
