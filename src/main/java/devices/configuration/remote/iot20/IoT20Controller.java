package devices.configuration.remote.iot20;

import devices.configuration.remote.IntervalRules;
import devices.configuration.remote.IntervalRulesRepository;
import devices.configuration.remote.Protocols;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
class IoT20Controller {

    public static final Protocols PROTOCOL = Protocols.IoT20;
    private final Clock clock;
    private final IntervalRulesRepository repository;

    @PostMapping(path = "/protocols/iot20/bootnotification/{deviceId}",
            consumes = "application/json", produces = "application/json")
    BootNotificationResponse handleBootNotification(@PathVariable String deviceId,
                                                    @RequestBody BootNotificationRequest request) {
        IntervalRules rules = repository.get();
        return new BootNotificationResponse(
                Instant.now(clock).toString(),
                (int) rules.calculateInterval(request.toDevice(deviceId, PROTOCOL)).toSeconds(),
                BootNotificationResponse.Status.Accepted);
    }
}
