package devices.configuration.maintenance;

import devices.configuration.data.DeviceRepository;
import devices.configuration.outbox.EventOutbox;
import devices.configuration.published.StationSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
class TriggerEventsController {

    private final DeviceRepository repository;
    private final EventOutbox outbox;

    @PostMapping(value = "/prv/events", params = {"type=StationSnapshot", "stationName"},
            produces = APPLICATION_JSON_VALUE)
    Stream<Object> emitStationSnapshot(String stationName, String apiKey) {
        ApiKeys.requireAny(apiKey);
        return repository.findByName(stationName)
                .map(StationSnapshot::updated)
                .stream()
                .flatMap(Collection::stream)
                .map(outbox::store);
    }
}
