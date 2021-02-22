package devices.configuration.features.bootNotification.protocol.validation;

import devices.configuration.features.bootNotification.ChargingStationManagementSystem;
import lombok.Getter;

public enum ProtocolInformationValidationMessage {
    STATION_NAME_IS_BLANK("station name is blank"),
    CSMS_IS_INCORRECT("csms is incorrect"),
    NOT_CSC_CSMS("csms should be " + ChargingStationManagementSystem.CHARGING_STATION_COMMUNICATION),
    PROTOCOL_NOT_SUPPORTED_BY_EGGPLANT("protocol is not supported by eggplant"),
    PROTOCOL_NOT_SUPPORTED_BY_CSC("protocol is not supported by csc"),
    MEDIA_TYPE_NOT_SUPPORTED_BY_CSC("given media type is not supported by csc"),
    ONLY_ONE_PROTOCOL_FIELD_IS_PROVIDED("only one protocol field is provided"),
    PROTOCOL_FIELDS_ARE_NOT_PROVIDED("protocol name or version is not provided");

    @Getter
    String message;
    ProtocolInformationValidationMessage(String message) {
        this.message = message;
    }
}
