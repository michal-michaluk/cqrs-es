package devices.configuration.features.communication.ocpp20;

import devices.configuration.features.communication.StationDetails;
import lombok.Value;

import static java.util.Optional.ofNullable;

@Value
class BootNotificationRequest {
    ChargingStation chargingStation;
    Reason reason;

    StationDetails toStationDetails() {
        if (chargingStation == null) {
            return new StationDetails(
                    new StationDetails.Hardware(null, null, null),
                    new StationDetails.Firmware(null),
                    new StationDetails.Modem(null, null),
                    new StationDetails.Meter(null, null)
            );
        }
        return new StationDetails(
                new StationDetails.Hardware(chargingStation.vendorName, chargingStation.model, chargingStation.serialNumber),
                new StationDetails.Firmware(chargingStation.firmwareVersion),
                new StationDetails.Modem(
                        ofNullable(chargingStation.modem).map(Modem::getIccid).orElse(null),
                        ofNullable(chargingStation.modem).map(Modem::getImsi).orElse(null)
                ),
                new StationDetails.Meter(null, null)
        );
    }

    @Value
    static class ChargingStation {
        String serialNumber;
        String model;
        Modem modem;
        String vendorName;
        String firmwareVersion;
    }

    @Value
    static class Modem {
        String iccid;
        String imsi;
    }

    enum Reason {
        ApplicationReset,
        FirmwareUpdate,
        LocalReset,
        PowerUp,
        RemoteReset,
        ScheduledReset,
        Triggered,
        Unknown,
        Watchdog
    }
}
