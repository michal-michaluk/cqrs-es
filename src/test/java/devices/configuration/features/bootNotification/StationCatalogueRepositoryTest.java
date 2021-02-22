package devices.configuration.features.bootNotification;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class StationCatalogueRepositoryTest {

    @Autowired
    StationsCatalogueRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
    }

    @Test
    void Should_set_created_at_and_modified_at_properly() {
        // given
        var station = stationCatalogueEntity();

        // when
        repo.save(station);
        var createdStation = repo.findByStationName(station.getStationName()).get();

        // then
        LocalDateTime createdAt = createdStation.getCreatedAt();
        assertThat(createdAt).isBetween(now().minusMinutes(1), now());

        // and when
        createdStation.setSoftwareVersion("somethingElse");
        repo.save(createdStation);
        var modifiedStation = repo.findByStationName(station.getStationName()).get();

        // then
        assertThat(modifiedStation.getModifiedAt()).isBetween(createdAt, now());
    }

    private StationsCatalogueEntity stationCatalogueEntity() {
        StationsCatalogueEntity station = new StationsCatalogueEntity("aNewStation");
        station.setMediaType("json");
        station.setProtocolVersion("1.6");
        station.setProtocolName("OCPP");
        station.setCsms("charging-station-communication");
        station.setSoftwareVersion("5.3.7");
        return station;
    }
}
