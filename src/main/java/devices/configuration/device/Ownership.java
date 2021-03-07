package devices.configuration.device;

import lombok.Value;

@Value
public class Ownership {
    String operator;
    String provider;

    public static Ownership unowned() {
        return new Ownership(null, null);
    }
}
