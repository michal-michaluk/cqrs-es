package devices.configuration.features.communication.ocpp16;


import lombok.Value;

import java.time.Instant;

@Value
class StatusNotificationRequest {

    int connectorId;
    ChargePointStatus status;
    ChargePointErrorCode errorCode;
    String info;
    Instant timestamp;
    String vendorId;
    String vendorErrorCode;

    enum ChargePointStatus {
        Available,
        Preparing,
        Charging,
        SuspendedEV,
        SuspendedEVSE,
        Finishing,
        Reserved,
        Faulted,
        Unavailable
    }

    enum ChargePointErrorCode {
        ConnectorLockFailure,
        EVCommunicationError,
        GroundFailure,
        HighTemperature,
        InternalError,
        LocalListConflict,
        NoError,
        OtherError,
        OverCurrentFailure,
        OverVoltage,
        PowerMeterFailure,
        PowerSwitchFailure,
        ReaderFailure,
        ResetFailure,
        UnderVoltage,
        WeakSignal
    }
}