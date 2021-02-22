package devices.configuration.features.bootNotification.protocol;

import lombok.Getter;

public enum OcppProtocolMediaType {
    SOAP("soap"),
    JSON("json"),
    INVALID("");

    @Getter
    String name;

    OcppProtocolMediaType(String name) {
        this.name = name;
    }

    static public OcppProtocolMediaType of(String value) {
        if(value == null) {
            return INVALID;
        }

        for (OcppProtocolMediaType mediaType: OcppProtocolMediaType.values()) {
            if (mediaType.name.equalsIgnoreCase(value)) {
                return mediaType;
            }
        }

        return INVALID;
    }

    public boolean isValid() {
        return !INVALID.equals(this);
    }
}
