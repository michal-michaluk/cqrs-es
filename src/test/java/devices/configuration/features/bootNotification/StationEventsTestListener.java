package devices.configuration.features.bootNotification;

import java.util.LinkedList;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class StationEventsTestListener {

    LinkedList<String> events = new LinkedList<>();

    @KafkaListener(topics = "station-configuration")
    void listenOnStationConnected(@Payload String payloadAsJson) {
        events.add(payloadAsJson);
    }
}
