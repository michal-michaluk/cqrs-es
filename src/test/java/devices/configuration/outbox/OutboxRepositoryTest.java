package devices.configuration.outbox;

import devices.configuration.EventTypes;
import devices.configuration.IntegrationTest;
import devices.configuration.legacy.EventsFixture;
import devices.configuration.legacy.StationLocationUpdated;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@IntegrationTest
class OutboxRepositoryTest {

    @Autowired
    OutboxMessageRepository repository;

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void Should_find_page_of_messages() {
        // given
        repository.saveAll(List.of(
                stationMetadataUpdateOf("EVB-junit-test1"),
                stationMetadataUpdateOf("EVB-junit-test2"),
                stationMetadataUpdateOf("EVB-junit-test3"),
                stationMetadataUpdateOf("EVB-junit-test4")
        ));

        // when
        Page<OutboxMessage> messages = repository.findFirstPage(2);

        // then
        Assertions.assertThat(messages).hasSize(2)
                .extracting(mes -> mes.getPayload(StationLocationUpdated.class).getStationName())
                .containsExactly("EVB-junit-test1", "EVB-junit-test2");
    }

    private OutboxMessage stationMetadataUpdateOf(String stationName) {
        return new OutboxMessage(
                UUID.randomUUID(),
                Instant.now(),
                EventTypes.of(EventsFixture.stationLocationUpdated(stationName)),
                EventsFixture.stationLocationUpdated(stationName)
        );
    }
}