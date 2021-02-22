package devices.configuration.legacy;

import devices.configuration.JsonDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
class KafkaEventsListener {

    private final LocationSynchronisationService locationService;
    private final JsonDeserializer jsonDeserializer;

    Map<EventType, Consumer<String>> handlers = Map.of(
            new EventType("StationMetadataUpdated"), this::handleStationMetadataUpdated,
            new EventType("LocationUpdated"), this::handleLocationUpdated
    );

    @KafkaListener(topics = "emobility-station")
    @KafkaListener(topics = "location-updates")
    void listenOnEggplantTopics(@Payload String payloadAsJson) {
        EventType eventType = jsonDeserializer.readValue(payloadAsJson, EventType.class, new EventType("_"));
        if (!handlers.containsKey(eventType)) {
            return;
        }
        handlers.get(eventType).accept(payloadAsJson);
    }

    void handleStationMetadataUpdated(String payloadAsJson) {
        StationMetadataUpdated stationUpdated = jsonDeserializer.readValue(payloadAsJson, StationMetadataUpdated.class, null);
        if (stationUpdated == null) {
            log.warn("Ignoring event {} from eggplant, can not parse json object", payloadAsJson);
            return;
        }
        locationService.syncStationLocation(List.of(stationUpdated.getStationName()), stationUpdated.toStationUpdate(), payloadAsJson);
    }

    void handleLocationUpdated(String payloadAsJson) {
        StationLocationUpdatedFromLegacy locationUpdated = jsonDeserializer.readValue(payloadAsJson, StationLocationUpdatedFromLegacy.class, null);
        if (locationUpdated == null) {
            log.warn("Ignoring event {} from eggplant, can not parse json object", payloadAsJson);
            return;
        }
        locationService.syncStationLocation(locationUpdated.getStations(), locationUpdated.toStationUpdate(), payloadAsJson);
    }
}
