package devices.configuration.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.DomainEvent;
import devices.configuration.EventTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOutbox {

    private final Clock clock;
    private final ObjectMapper mapper;
    private final OutboxMessageRepository outboxRepository;
    private final KafkaTemplate<String, String> kafka;
    private final OutboxConfiguration configuration;

    @Value("${outbox.batch:1000}")
    private int batchSize;

    @EventListener
    public OutboxMessage store(DomainEvent event) {
        EventTypes.Type type = EventTypes.of(event);
        if (configuration.definedFor(event)) {
            OutboxMessage message = new OutboxMessage(
                    UUID.randomUUID(), Instant.now(clock), type, event
            );
            outboxRepository.save(message);
            return message;
        }
        return null;
    }

    @Scheduled(fixedDelayString = "${outbox.delay:PT10S}")
    void send() throws JsonProcessingException {
        send(outboxRepository.findFirstPage(batchSize));
    }

    private void send(Page<OutboxMessage> messages) throws JsonProcessingException {
        for (OutboxMessage message : messages) {
            DomainEvent payload = message.getPayload();
            var config = configuration.ofEvent(payload);
            String json = mapper.writeValueAsString(message);
            kafka.send(config.getTopic(), config.partitionKey(payload), json)
                    .addCallback(
                            success -> {
                                outboxRepository.deleteByIdInSeparateTransaction(message.getEventId());
                                log.info("Successfully send outbox message topic: {}, eventId: {}, payload: {}",
                                        config.getTopic(), message.getEventId(), json);
                            },
                            exception ->
                                    log.warn("Could not send outbox message topic: {}, eventId: {}, payload: {}",
                                            config.getTopic(), message.getEventId(), json, exception)
                    );
        }
    }
}
