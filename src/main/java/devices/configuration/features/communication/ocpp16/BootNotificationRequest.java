package devices.configuration.features.communication.ocpp16;

import devices.configuration.features.communication.StationDetails;
import lombok.Value;

@Value
class BootNotificationRequest {
    String chargePointVendor;
    String chargePointModel;
    String chargePointSerialNumber;
    String chargeBoxSerialNumber;
    String firmwareVersion;
    String iccid;
    String imsi;
    String meterType;
    String meterSerialNumber;

    StationDetails toStationDetails() {
        return new StationDetails(
                new StationDetails.Hardware(chargePointVendor, chargePointModel, chargePointSerialNumber),
                new StationDetails.Firmware(firmwareVersion),
                new StationDetails.Modem(iccid, imsi),
                new StationDetails.Meter(meterType, meterSerialNumber)
        );
    }
}
