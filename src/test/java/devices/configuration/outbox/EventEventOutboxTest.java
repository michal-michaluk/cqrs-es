package devices.configuration.outbox;

import devices.configuration.IntegrationTest;
import devices.configuration.features.bootNotification.StationConnectedToCsc;
import devices.configuration.legacy.EventsFixture;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class EventEventOutboxTest {
    @Autowired
    OutboxMessageRepository repository;
    @Autowired
    EventOutbox outbox;
    @Autowired
    OutgoingEventsTestListener outgoing;

    @BeforeEach
    void setUp() {
        repository.clear();
        outgoing.clear();
    }

    @Test
    void Should_store_message() {
        // when
        outbox.store(EventsFixture.stationConnectedToCsc("EVB-junit-test-Should_store_message"));

        // then
        List<OutboxMessage> messages = repository.findAll();
        Assertions.assertThat(messages).hasSize(1)
                .extracting(outboxMessage -> outboxMessage.getPayload(StationConnectedToCsc.class).getStationName())
                .contains("EVB-junit-test-Should_store_message");
    }

    @Test
    void Should_ignore_not_configured_events() {
        // when
        outbox.store(EventsFixture.fakeDomainEvent());

        // then
        List<OutboxMessage> messages = repository.findAll();
        Assertions.assertThat(messages).hasSize(0);
    }

    @Test
    void Should_send_message() {
        // given
        outbox.store(EventsFixture.stationConnectedToCsc("Should_send_message"));

        // then
        outgoing.hasExactly(1, OutgoingEventsTestListener.event()
                .withStationName("Should_send_message")
        );

        // and
        Awaitility.await().untilAsserted(
                () -> Assertions.assertThat(repository.findAll()).isEmpty()
        );
    }
}
