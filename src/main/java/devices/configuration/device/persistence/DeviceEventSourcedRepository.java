package devices.configuration.device.persistence;

import devices.configuration.DomainEvent;
import devices.configuration.Events;
import devices.configuration.JsonConfiguration;
import devices.configuration.NotVersionedTypes;
import devices.configuration.device.*;
import devices.configuration.device.events.DeviceAssigned;
import devices.configuration.device.events.LocationSpecified;
import devices.configuration.device.events.OpeningHoursSpecified;
import devices.configuration.device.events.SettingsChanged;
import devices.configuration.device.persistence.events.LegacyDomainEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DeviceEventSourcedRepository implements DeviceRepository {

    private static final NotVersionedTypes<DomainEvent> TYPES = NotVersionedTypes.classToNotVersionedType(
            JsonConfiguration.OBJECT_MAPPER, DomainEvent.class
    );

    private final DeviceEntityRepository objects;
    private final DeviceEventRepository events;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;

    @Override
    public Optional<Device> get(String deviceId) {
        Optional<DeviceEntity> root = objects.findByDeviceId(deviceId);
        if (root.isEmpty()) {
            return Optional.empty();
        }
        List<DeviceEventEntity> entities = events.findLastEvents(deviceId);
        Events events = Events.groupByClass(
                entities.stream()
                        .map(DeviceEventEntity::getEvent)
                        .map(LegacyDomainEvent::migrateIfNecessary)
        );
        return Optional.of(new Device(
                deviceId,
                new ArrayList<>(),
                events.getLast(DeviceAssigned.class)
                        .map(DeviceAssigned::getOwnership)
                        .orElse(Ownership.unowned()),
                events.getLast(LocationSpecified.class)
                        .map(LocationSpecified::getLocation)
                        .orElse(null),
                events.getLast(OpeningHoursSpecified.class)
                        .map(OpeningHoursSpecified::getOpeningHours)
                        .orElse(OpeningHours.alwaysOpen()),
                events.getLast(SettingsChanged.class)
                        .map(SettingsChanged::getSettings)
                        .orElse(Settings.defaultSettings())
        ));
    }

    @Override
    public void save(Device device) {
        List<DomainEvent> events = device.getEvents();
        if (events.isEmpty()) return;
        events.stream()
                .map(event -> new DeviceEventEntity(
                        UUID.randomUUID(),
                        Instant.now(clock),
                        device.getDeviceId(),
                        TYPES.get(event.getClass()),
                        event))
                .forEach(this.events::save);
        events.forEach(publisher::publishEvent);
        publisher.publishEvent(device.toSnapshot());
        events.clear();
    }
}
