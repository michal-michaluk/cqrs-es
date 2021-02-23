package devices.configuration.communication.iot20;

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
