package devices.configuration.features.toggle;

import lombok.Value;

@Value
public class NewToggle {
    String name;
    boolean initialValue;

    public boolean getInitialValue() {
        return initialValue;
    }
}
