package devices.configuration.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Deviceish {
    String deviceId;
    String vendor;
    String model;
    Protocols protocol;
}
