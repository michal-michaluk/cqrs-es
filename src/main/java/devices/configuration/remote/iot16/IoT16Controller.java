package devices.configuration.remote.iot16;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
class IoT16Controller {

    private final Clock clock;

    @PostMapping(path = "/protocols/iot16/bootnotification/{deviceId}",
            consumes = "application/json", produces = "application/json")
    BootNotificationResponse handleBootNotification(@PathVariable String deviceId,
                                                    @RequestBody BootNotificationRequest request) {
        return new BootNotificationResponse(
                Instant.now(clock).toString(),
                1800,
                BootNotificationResponse.Status.Accepted);
    }

}
