package devices.configuration.device.persistence.events;

import devices.configuration.DomainEvent;
import devices.configuration.device.Settings;
import devices.configuration.device.events.SettingsChanged;
import lombok.Value;

public class SettingsChangedV1 implements LegacyDomainEvent {
    String deviceId;
    SettingsLegacy settings;

    @Override
    public DomainEvent migrate() {
        return new SettingsChanged(deviceId, Settings.builder()
                .autoStart(settings.getAutoStart())
                .remoteControl(settings.getRemoteControl())
                .billing(false)
                .reimbursement(false)
                .showOnMap(settings.getShow())
                .publicAccess(settings.getAccessible())
                .build());
    }

    @Value
    public class SettingsLegacy {
        Boolean autoStart;
        Boolean remoteControl;

        Boolean billing;
        Boolean reimbursement;

        Boolean show;
        Boolean accessible;
    }
}
