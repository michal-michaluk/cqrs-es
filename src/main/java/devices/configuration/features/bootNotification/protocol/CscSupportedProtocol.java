package devices.configuration.features.bootNotification.protocol;

import lombok.Getter;

import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.JSON;

@Getter
public enum CscSupportedProtocol {
    OCPP_1_6_JSON(EggplantProtocolName.OCPP, OcppProtocolVersion.V_1_6, JSON),
    OCPP_2_0(EggplantProtocolName.OCPP, OcppProtocolVersion.V_2_0, JSON),
    INVALID(EggplantProtocolName.INVALID, OcppProtocolVersion.INVALID, OcppProtocolMediaType.INVALID);

    EggplantProtocolName protocolName;
    OcppProtocolVersion protocolVersion;
    OcppProtocolMediaType mediaType;

    CscSupportedProtocol(EggplantProtocolName protocolName, OcppProtocolVersion protocolVersion, OcppProtocolMediaType mediaType) {
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
        this.mediaType = mediaType;
    }

    public static CscSupportedProtocol of(String protocolName, String protocolVersion) {
        final OcppProtocolVersion ocppProtocolVersion = OcppProtocolVersion.of(protocolVersion);
        if (protocolName == null || protocolVersion == null || !ocppProtocolVersion.isValid() || !isOcpp(protocolName)) {
            return INVALID;
        }

        for (CscSupportedProtocol entry: CscSupportedProtocol.values()) {
            if (ocppProtocolVersion.equals(entry.protocolVersion)) {
                return entry;
            }
        }

        return INVALID;
    }

    public boolean isValid() {
        return !INVALID.equals(this);
    }

    private static boolean isOcpp(String protocolName) {
        return protocolName.equalsIgnoreCase(EggplantProtocolName.OCPP.getName());
    }
}
