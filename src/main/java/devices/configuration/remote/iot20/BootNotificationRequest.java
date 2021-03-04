package devices.configuration.remote.iot20;

import devices.configuration.remote.Deviceish;
import devices.configuration.remote.Protocol;
import lombok.Value;

@Value
class BootNotificationRequest {
    Device device;
    Reason reason;

    public Deviceish toDevice(String deviceId) {
        return new Deviceish(
                deviceId,
                device.vendorName,
                device.model,
                Protocol.IOT16
        );
    }

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
}
