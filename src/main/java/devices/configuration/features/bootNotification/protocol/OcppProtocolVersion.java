package devices.configuration.features.bootNotification.protocol;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum OcppProtocolVersion {
    V_1_2 ("1.2"),
    V_1_5 ("1.5"),
    V_1_6 ("1.6"),
    V_2_0 ("2.0"),
    INVALID("");

    @Getter
    String name;

    OcppProtocolVersion(String name) {
        this.name = name;
    }

    public static OcppProtocolVersion of(String protocolVersion) {
        if(protocolVersion == null) {
            return INVALID;
        }

        Pattern pattern = Pattern.compile("(\\d\\.\\d)((\\.)(.*))*");
        Matcher matcher = pattern.matcher(protocolVersion);

        try {
            if (matcher.matches()) {
                String version = matcher.group(1);
                for (OcppProtocolVersion protocol: OcppProtocolVersion.values()) {
                    if (protocol.name.equalsIgnoreCase(version)) {
                        return protocol;
                    }
                }
            }
        } catch (Exception ex) {
            return INVALID;
        }

        return INVALID;
    }

    public boolean isValid() {
        return !INVALID.equals(this);
    }
}
