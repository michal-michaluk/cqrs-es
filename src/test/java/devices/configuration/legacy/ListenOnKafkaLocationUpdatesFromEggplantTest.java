package devices.configuration.legacy;

import devices.configuration.IntegrationTest;
import devices.configuration.features.catalogue.StationsFixture;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.catalogue.StationsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@IntegrationTest
class ListenOnKafkaLocationUpdatesFromEggplantTest {

    private final int TIME_TO_WAIT_FOR_KAFKA_IN_SECONDS = 5;

    @Autowired
    EmobilityStationTopic topics;
    @Autowired
    StationsService service;
    @Autowired
    StationsRepository repository;

    @Test
    void Should_change_station_location_after_StationMetadataUpdated() {
        //given
        var stationName = UUID.randomUUID().toString();
        repository.save(StationsFixture.evb(stationName)
                .setLocation(StationsFixture.Locations.rooseveltlaanInGent())
        );

        //when
        topics.stationMetadataUpdated(stationName, StationsFixture.Locations.dusartstraatInAmsterdam());

        //then
        await().atMost(Duration.ofSeconds(TIME_TO_WAIT_FOR_KAFKA_IN_SECONDS)).untilAsserted(() ->
                assertThat(service.findByName(stationName))
                        .hasValueSatisfying(s ->
                                assertThat(s.getLocation())
                                        .isEqualTo(StationsFixture.Locations.dusartstraatInAmsterdam())
                        )
        );
    }

    @Test
    void Should_change_station_location_after_LocationUpdated() {
        //given
        var stationName = UUID.randomUUID().toString();
        repository.save(StationsFixture.evb(stationName)
                .setLocation(StationsFixture.Locations.rooseveltlaanInGent())
        );

        //when
        topics.locationUpdated(List.of(stationName), StationsFixture.Locations.rooseveltlaanInGent(), StationsFixture.Locations.dusartstraatInAmsterdam());

        //then
        await().atMost(Duration.ofSeconds(TIME_TO_WAIT_FOR_KAFKA_IN_SECONDS)).untilAsserted(() ->
                assertThat(service.findByName(stationName))
                        .hasValueSatisfying(s ->
                                assertThat(s.getLocation())
                                        .isEqualTo(StationsFixture.Locations.dusartstraatInAmsterdam())
                        )
        );
    }
}
