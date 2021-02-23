package devices.configuration.remote;

import lombok.Value;

@Value
public class IntervalRules {

    int def;

    public static IntervalRules defaultRule() {
        return new IntervalRules(1800);
    }
}
