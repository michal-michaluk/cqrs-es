package devices.configuration.remote.iot20;

import devices.configuration.remote.Deviceish;
import devices.configuration.remote.Protocols;
import lombok.Value;

@Value
class BootNotificationRequest {
    Device device;
    Reason reason;

    @Value
    static class Device {
        String serialNumber;
        String model;
        Modem modem;
        String vendorName;
        String firmwareVersion;
    }

    @Value
    static class Modem {
        String iccid;
        String imsi;
    }

    enum Reason {
        ApplicationReset,
        FirmwareUpdate,
        LocalReset,
        PowerUp,
        RemoteReset,
        ScheduledReset,
        Triggered,
        Unknown,
        Watchdog
    }

    Deviceish toDevice(String deviceId, Protocols protocol) {
        return new Deviceish(deviceId, device.getVendorName(), device.getModel(), protocol);
    }
}
