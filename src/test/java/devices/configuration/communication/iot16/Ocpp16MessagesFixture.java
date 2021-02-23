package devices.configuration.communication.iot16;

public class Ocpp16MessagesFixture {

    public BootNotificationRequest bootNotification() {
        return new BootNotificationRequest(
                "Garo",
                "CPF25 Family",
                "820394A93203",
                "891234A56711",
                "1.1",
                "112233445566778899C1",
                "082931213347973812",
                "5051",
                "937462A48276"
        );
    }
}
