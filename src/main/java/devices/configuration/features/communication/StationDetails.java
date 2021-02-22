package devices.configuration.features.communication;

import lombok.Value;

@Value
public class StationDetails {
    Hardware hardware;
    Firmware firmware;
    Modem modem;
    Meter meter;

    @Value
    public static class Hardware {
        String vendor;
        String model;
        String serialNumber;
    }

    @Value
    public static class Firmware {
        String currentVersion;
    }

    @Value
    public static class Modem {
        String iccid;
        String imsi;
    }

    @Value
    public static class Meter {
        String type;
        String serialNumber;
    }
}
