package devices.configuration.features.communication;

import devices.configuration.features.communication.ocpp16.Ocpp16ProtocolUnification;
import devices.configuration.features.communication.ocpp20.Ocpp20ProtocolUnification;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {

    OCPP201J("/ocpp20/azure", new Ocpp20ProtocolUnification()),
    OCPP20J("/ocpp20/azure", new Ocpp20ProtocolUnification()),
    OCPP16J("/ocpp/azure", new Ocpp16ProtocolUnification());

    String legacyPath;
    ProtocolUnification unification;
}
