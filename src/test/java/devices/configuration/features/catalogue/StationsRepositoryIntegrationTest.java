package devices.configuration.features.catalogue;

import devices.configuration.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class StationsRepositoryIntegrationTest {

    @Autowired
    StationsRepository stationRepository;

    @Test
    public void Should_save_stationId_as_uuid() {

        // given
        Station station = StationsFixture.evb();

        // when
        stationRepository.save(station);
        Optional<Station> saved = stationRepository.findByName(station.getName());

        // then
        Assertions.assertThat(saved).map(Station::getId).isNotNull();
        Assertions.assertThat(saved).map(Station::getAddedOn).isNotNull();
        Assertions.assertThat(saved).map(Station::getModifiedOn).isNotNull();
    }

    @Test
    public void Should_update_modified_date() {

        // given
        Station station = StationsFixture.evb();
        stationRepository.save(station);
        Instant modifiedOn1 = stationRepository.findByName(station.getName()).map(Station::getModifiedOn).orElse(null);

        // when
        station.setOpeningHours(StationsFixture.Opening.openAtWorkWeek());
        stationRepository.save(station);
        Instant modifiedOn2 = stationRepository.findByName(station.getName()).map(Station::getModifiedOn).orElse(null);

        // then
        assertThat(modifiedOn1).isBefore(modifiedOn2);
    }

    @Test
    public void Should_save_station_location() {

        // given
        Station station = StationsFixture.evb();

        // when
        stationRepository.save(station);
        Station saved = stationRepository.findByName(station.getName()).orElse(null);

        // then
        assertThat(saved).isNotNull()
                .extracting(Station::getLocation)
                .isEqualTo(
                        StationsFixture.Locations.rooseveltlaanInGent()
                );
    }

    @Test
    public void Should_save_station_opening_hours() {

        // given
        Station station = StationsFixture.evb()
                .setOpeningHours(StationsFixture.Opening.openAtWorkWeek());

        // when
        stationRepository.save(station);
        Station saved = stationRepository.findByName(station.getName()).orElse(null);

        // then
        assertThat(saved).isNotNull()
                .extracting(Station::getOpeningHours)
                .isEqualTo(StationsFixture.Opening.openAtWorkWeek());
    }
}
