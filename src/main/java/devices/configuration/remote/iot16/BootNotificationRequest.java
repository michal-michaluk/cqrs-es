package devices.configuration.remote.iot16;

import devices.configuration.remote.Deviceish;
import devices.configuration.remote.Protocol;
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

    public Deviceish toDevice(String deviceId) {
        return new Deviceish(
                deviceId,
                chargePointVendor,
                chargePointModel,
                Protocol.IOT16
        );
    }
}
