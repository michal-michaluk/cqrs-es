package devices.configuration.features.communication.ocpp20;


import lombok.Value;

import java.time.Instant;

@Value
class StatusNotificationRequest {

    Instant timestamp;
    ConnectorStatusEnumType connectorStatus;
    int evseId;
    int connectorId;

    enum ConnectorStatusEnumType {
        Available,
        Occupied,
        Reserved,
        Faulted
    }
}
