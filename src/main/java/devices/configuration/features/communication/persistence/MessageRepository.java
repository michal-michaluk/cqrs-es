package devices.configuration.features.communication.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.features.communication.Endpoint;
import devices.configuration.features.communication.StationDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MessageRepository {

    private final Clock clock;
    private final ObjectMapper mapper;
    private final MessageJpaRepository repo;

    public void save(String station, Endpoint endpoint, Class<?> type, String payload) {
        repo.save(new StationMessage(
                UUID.randomUUID(),
                Instant.now(clock),
                station,
                endpoint,
                type.getSimpleName(),
                payload
        ));
    }

    public <T> Optional<T> get(String station, Class<T> type) {
        var optionalEntity = repo.findFirst1ByStationAndTypeOrderByTimeDesc(station, type.getSimpleName());

        return optionalEntity
                .map(StationMessage::getPayload)
                .map(payload -> {
                    try {
                        return mapper.readValue(payload, type);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Optional<StationDetails> getStationDetails(String station) {
        return repo.findFirst1ByStationAndTypeOrderByTimeDesc(station, "BootNotificationRequest")
                .map(entity ->
                        entity
                                .getEndpoint()
                                .getUnification()
                                .toStationDetails(mapper, entity.getPayload())
                );
    }
}
