package devices.configuration.data;

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
    DeviceRepository stationRepository;

    @Test
    public void Should_save_stationId_as_uuid() {

        // given
        Device station = StationsFixture.evb();

        // when
        stationRepository.save(station);
        Optional<Device> saved = stationRepository.findByName(station.getName());

        // then
        Assertions.assertThat(saved).map(Device::getId).isNotNull();
        Assertions.assertThat(saved).map(Device::getAddedOn).isNotNull();
        Assertions.assertThat(saved).map(Device::getModifiedOn).isNotNull();
    }

    @Test
    public void Should_update_modified_date() {

        // given
        Device station = StationsFixture.evb();
        stationRepository.save(station);
        Instant modifiedOn1 = stationRepository.findByName(station.getName()).map(Device::getModifiedOn).orElse(null);

        // when
        station.setOpeningHours(StationsFixture.Opening.openAtWorkWeek());
        stationRepository.save(station);
        Instant modifiedOn2 = stationRepository.findByName(station.getName()).map(Device::getModifiedOn).orElse(null);

        // then
        assertThat(modifiedOn1).isBefore(modifiedOn2);
    }

    @Test
    public void Should_save_station_location() {

        // given
        Device station = StationsFixture.evb();

        // when
        stationRepository.save(station);
        Device saved = stationRepository.findByName(station.getName()).orElse(null);

        // then
        assertThat(saved).isNotNull()
                .extracting(Device::getLocation)
                .isEqualTo(
                        StationsFixture.Locations.rooseveltlaanInGent()
                );
    }

    @Test
    public void Should_save_station_opening_hours() {

        // given
        Device station = StationsFixture.evb()
                .setOpeningHours(StationsFixture.Opening.openAtWorkWeek());

        // when
        stationRepository.save(station);
        Device saved = stationRepository.findByName(station.getName()).orElse(null);

        // then
        assertThat(saved).isNotNull()
                .extracting(Device::getOpeningHours)
                .isEqualTo(StationsFixture.Opening.openAtWorkWeek());
    }
}
