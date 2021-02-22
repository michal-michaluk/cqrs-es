package devices.configuration.features.communication;

public class UnifiedMessagesFixture {

    public static StationDetails stationDetailsGaroWithMeter(String firmware) {
        return new StationDetails(
                new StationDetails.Hardware("Garo", "CPF25 Family", "820394A93203"),
                new StationDetails.Firmware(firmware),
                new StationDetails.Modem("112233445566778899C1", "082931213347973812"),
                new StationDetails.Meter("5051", "937462A48276")
        );
    }

    public static StationDetails stationDetailsChargePoint(String firmware) {
        return new StationDetails(
                new StationDetails.Hardware("ChargePoint", "CPF25 Family", "820394A93203"),
                new StationDetails.Firmware(firmware),
                new StationDetails.Modem("1122 3344 5566 7788 99 C 1", "082931213347973812"),
                new StationDetails.Meter(null, null)
        );
    }

    public static StationDetails stationDetailsWithoutModemAndMeter(String firmware) {
        return new StationDetails(
                new StationDetails.Hardware("ChargePoint", "CPF25 Family", "820394A93203"),
                new StationDetails.Firmware(firmware),
                new StationDetails.Modem(null, null),
                new StationDetails.Meter(null, null)
        );
    }

    public static StationDetails stationDetailsWithOnlyNulls() {
        return new StationDetails(
                new StationDetails.Hardware(null, null, null),
                new StationDetails.Firmware(null),
                new StationDetails.Modem(null, null),
                new StationDetails.Meter(null, null)
        );
    }
}
