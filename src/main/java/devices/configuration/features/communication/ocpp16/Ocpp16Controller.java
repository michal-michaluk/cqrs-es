package devices.configuration.features.communication.ocpp16;

import devices.configuration.features.communication.BootIntervals;
import devices.configuration.features.communication.Endpoint;
import devices.configuration.features.communication.LegacySystem;
import devices.configuration.features.communication.persistence.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
class Ocpp16Controller {

    private static final Endpoint ENDPOINT = Endpoint.OCPP16J;
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssVV");

    private final Clock clock;
    private final BootIntervals intervals;
    private final MessageRepository repository;
    private final LegacySystem legacy;

    @PostMapping(path = {
            "/ocpp/azure/bootnotification/{station}",
            "/int/protocols/ocpp16/bootnotification/{station}"
    }, consumes = "application/json", produces = "application/json")
    BootNotificationResponse handleBootNotification(@PathVariable String station,
                                                    @RequestBody String request) {
        legacy.bootNotification(ENDPOINT, station, request);
        repository.save(station, ENDPOINT, BootNotificationRequest.class, request);
        return new BootNotificationResponse(
                ZonedDateTime.now(clock).format(TIMESTAMP),
                intervals.heartbeatInterval(ENDPOINT),
                BootNotificationResponse.Status.Accepted);
    }

    @PostMapping(path = {
            "/int/protocols/ocpp16/statusnotification/{station}"
    }, consumes = "application/json")
    public StatusNotificationResponse handleStatusNotification(@PathVariable String station,
                                                               @RequestBody String request) {
        legacy.statusNotification(ENDPOINT, station, request);
        repository.save(station, ENDPOINT, StatusNotificationRequest.class, request);
        return new StatusNotificationResponse();
    }
}
