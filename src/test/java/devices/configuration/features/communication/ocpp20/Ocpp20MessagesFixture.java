package devices.configuration.features.communication.ocpp20;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class Ocpp20MessagesFixture {
    public static final Class<BootNotificationRequest> BOOT_TYPE = BootNotificationRequest.class;
    private static final ObjectMapper mapper = new ObjectMapper();

    public BootNotificationRequest bootNotification() {
        return bootNotification("1.1");
    }

    public BootNotificationRequest bootNotification(String firmwareVersion) {
        return new BootNotificationRequest(
                new BootNotificationRequest.ChargingStation(
                        "820394A93203",
                        "CPF25 Family",
                        new BootNotificationRequest.Modem(
                                "1122 3344 5566 7788 99 C 1",
                                "082931213347973812"
                        ),
                        "ChargePoint",
                        firmwareVersion
                ),
                BootNotificationRequest.Reason.PowerUp
        );
    }

    public BootNotificationRequest bootNotificationWithNullModem(String firmware) {
        return new BootNotificationRequest(
                new BootNotificationRequest.ChargingStation(
                        "820394A93203",
                        "CPF25 Family",
                        null,
                        "ChargePoint",
                        firmware
                ),
                BootNotificationRequest.Reason.PowerUp
        );
    }

    public BootNotificationRequest bootNotificationWithNullStation() {
        return new BootNotificationRequest(null,
                BootNotificationRequest.Reason.Unknown
        );
    }

    @SneakyThrows
    public static String json(Object object) {
        return mapper.writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T json(String json, Class<T> type) {
        return mapper.readValue(json, type);
    }
}
