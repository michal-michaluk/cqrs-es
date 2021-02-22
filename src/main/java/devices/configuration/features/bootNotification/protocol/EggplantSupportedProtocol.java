package devices.configuration.features.bootNotification.protocol;

import lombok.Getter;

import static devices.configuration.features.bootNotification.protocol.EggplantProtocolName.*;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.JSON;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.SOAP;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolVersion.*;

@Getter
public enum EggplantSupportedProtocol {
    OCPP_1_2(OCPP, V_1_2, SOAP),
    OCPP_1_5(OCPP, V_1_5, SOAP),
    OCPP_1_6_SOAP(OCPP, V_1_6, SOAP),
    OCPP_1_6_JSON(OCPP_J, V_1_6, JSON),
    OCPP_1_6_AZURE_JSON(OCPP_AZURE, V_1_6, JSON),
    OCPP_2_0(OCPP_AZURE, V_2_0, JSON),
    INVALID(EggplantProtocolName.INVALID, OcppProtocolVersion.INVALID, OcppProtocolMediaType.INVALID);

    EggplantProtocolName protocolName;
    OcppProtocolVersion protocolVersion;
    OcppProtocolMediaType mediaType;

    EggplantSupportedProtocol(EggplantProtocolName protocolName, OcppProtocolVersion protocolVersion, OcppProtocolMediaType mediaType) {
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
        this.mediaType = mediaType;
    }

    public static EggplantSupportedProtocol of(String protocolName, String protocolVersion) {
        final EggplantProtocolName eggplantProtocolName = EggplantProtocolName.of(protocolName);
        final OcppProtocolVersion ocppProtocolVersion = OcppProtocolVersion.of(protocolVersion);
        if (!eggplantProtocolName.isValid() || !ocppProtocolVersion.isValid()) {
            return INVALID;
        }

        for (EggplantSupportedProtocol entry: EggplantSupportedProtocol.values()) {
            if (protocolName.equalsIgnoreCase(entry.protocolName.getName()) &&
                    ocppProtocolVersion.equals(entry.protocolVersion) &&
                    eggplantProtocolName.getMediaType().equals(entry.getMediaType())) {
                return entry;
            }
        }

        return INVALID;
    }

    public boolean isValid() {
        return !INVALID.equals(this);
    }
}
