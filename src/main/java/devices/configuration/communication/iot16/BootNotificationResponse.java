package devices.configuration.communication.iot16;

import lombok.Value;

@Value
class BootNotificationResponse {
    String currentTime;
    int interval;
    Status status;

    enum Status {
        Accepted,
        Pending,
        Rejected
    }
}
