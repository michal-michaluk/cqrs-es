package devices.configuration.remote;

import lombok.Value;

@Value
public class Deviceish {
    String deviceId;
    String vendor;
    String model;
    Protocol protocol;
}
