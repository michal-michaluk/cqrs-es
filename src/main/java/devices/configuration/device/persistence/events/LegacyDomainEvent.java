package devices.configuration.device.persistence.events;

import devices.configuration.DomainEvent;

public interface LegacyDomainEvent extends DomainEvent {
    DomainEvent migrate();

    static DomainEvent migrateIfNecessary(DomainEvent event) {
        if (event instanceof LegacyDomainEvent) {
            return ((LegacyDomainEvent) event).migrate();
        } else {
            return event;
        }
    }
}
