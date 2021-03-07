package devices.configuration.device.events;

import devices.configuration.DomainEvent;
import devices.configuration.device.Settings;

public final class SettingsChanged implements DomainEvent {
    private final String deviceId;
    private final Settings settings;

    @java.beans.ConstructorProperties({"deviceId", "settings"})
    public SettingsChanged(String deviceId, Settings settings) {
        this.deviceId = deviceId;
        this.settings = settings;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof SettingsChanged)) return false;
        final SettingsChanged other = (SettingsChanged) o;
        final Object this$deviceId = this.getDeviceId();
        final Object other$deviceId = other.getDeviceId();
        if (this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId)) return false;
        final Object this$settings = this.getSettings();
        final Object other$settings = other.getSettings();
        if (this$settings == null ? other$settings != null : !this$settings.equals(other$settings)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $deviceId = this.getDeviceId();
        result = result * PRIME + ($deviceId == null ? 43 : $deviceId.hashCode());
        final Object $settings = this.getSettings();
        result = result * PRIME + ($settings == null ? 43 : $settings.hashCode());
        return result;
    }

    public String toString() {
        return "SettingsChanged(deviceId=" + this.getDeviceId() + ", settings=" + this.getSettings() + ")";
    }
}
