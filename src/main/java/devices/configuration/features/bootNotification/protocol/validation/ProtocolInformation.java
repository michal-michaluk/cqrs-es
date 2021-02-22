package devices.configuration.features.bootNotification.protocol.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProtocolInformation {
    String stationName;
    String protocolName;
    String protocolVersion;
    String csms;
    String mediaType;
}
