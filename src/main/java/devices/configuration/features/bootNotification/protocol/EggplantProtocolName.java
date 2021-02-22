package devices.configuration.features.bootNotification.protocol;

import devices.configuration.features.bootNotification.ChargingStationManagementSystem;
import lombok.Getter;

import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.JSON;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.SOAP;

public enum EggplantProtocolName {
    OCPP("ocpp"),
    OCPP_J("ocpp-j"),
    OCPP_AZURE("ocpp-azure"),
    INVALID("");

    @Getter
    String name;

    EggplantProtocolName(String name) {
        this.name = name;
    }

    public static EggplantProtocolName of(String name) {
        if(name == null) {
            return INVALID;
        }

        for (EggplantProtocolName protocolName: EggplantProtocolName.values()) {
            if (protocolName.name.equalsIgnoreCase(name)) {
                return protocolName;
            }
        }

        return INVALID;
    }

    public static EggplantProtocolName fromCsms(String csms, String mediaType) {
        final ChargingStationManagementSystem chargingStationManagementSystem = ChargingStationManagementSystem.of(csms);
        if(chargingStationManagementSystem.isValid()) {
            switch (chargingStationManagementSystem) {
                case OLD_PLATFORM:
                    final OcppProtocolMediaType ocppProtocolMediaType = OcppProtocolMediaType.of(mediaType);
                    if(ocppProtocolMediaType.isValid()) {
                        switch (ocppProtocolMediaType) {
                            case SOAP: return OCPP;
                            case JSON: return OCPP_J;
                            case INVALID: return INVALID;
                        }
                    }
                    return INVALID;
                case CHARGING_STATION_COMMUNICATION: return OCPP_AZURE;
                case INVALID: return INVALID;
            }
        }
        return INVALID;
    }

    public boolean isValid() {
        return !INVALID.equals(this);
    }

    public ChargingStationManagementSystem getCsms() {
        switch (this) {
            case OCPP:
            case OCPP_J:
                return ChargingStationManagementSystem.OLD_PLATFORM;
            case OCPP_AZURE:
                return ChargingStationManagementSystem.CHARGING_STATION_COMMUNICATION;
            default:
                return ChargingStationManagementSystem.INVALID;
        }
    }

    public OcppProtocolMediaType getMediaType() {
        switch (this) {
            case OCPP:
                return SOAP;
            case OCPP_J:
            case OCPP_AZURE:
                return JSON;
            default:
                return OcppProtocolMediaType.INVALID;
        }
    }
}
