package devices.configuration.features.communication.ocpp16;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class Ocpp16MessagesFixture {
    public static final Class<BootNotificationRequest> BOOT_TYPE = BootNotificationRequest.class;
    private static final ObjectMapper mapper = new ObjectMapper();

    public BootNotificationRequest bootNotification() {
        return bootNotification("1.1");
    }

    public BootNotificationRequest bootNotification(String firmwareVersion) {
        return new BootNotificationRequest(
                "Garo",
                "CPF25 Family",
                "820394A93203",
                "891234A56711",
                firmwareVersion,
                "112233445566778899C1",
                "082931213347973812",
                "5051",
                "937462A48276"
        );
    }

    public BootNotificationRequest bootNotificationWithOnlyNulls() {
        return new BootNotificationRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
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
