package devices.configuration.data;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static devices.configuration.data.StationsResponseAssert.assertThat;

@IntegrationTest
class StationOpeningHoursIntegrationTest {

    @Autowired
    RequestFixture requestFixture;

    @Autowired
    DeviceRepository repository;

    @Test
    void Should_get_always_open_for_station_without_opening_hours() {
        //given
        Device station = StationsFixture.evb()
                .setOpeningHours(null);
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.getStation(station.getName());

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasDefaultOpeningHours()
                );
    }

    @Test
    void Should_get_station_with_opening_hours() {
        //given
        Device station = StationsFixture.evb()
                .setOpeningHours(StationsFixture.Opening.openAtWorkWeek());
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.getStation(station.getName());

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasOpeningHours(StationsFixture.Opening.openAtWorkWeek())
                );
    }

    @Test
    void Should_set_station_opening_hours() {
        //given
        Device station = StationsFixture.evb()
                .setOpeningHours(StationsFixture.Opening.alwaysOpen());
        repository.save(station);
        var openingHours = StationsFixture.Opening.openAtWorkWeek();

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new UpdateDevice().setOpeningHours(openingHours));

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasOpeningHours(StationsFixture.Opening.openAtWorkWeek())
                );
    }

    @Test
    void Should_reset_station_opening_hours() {
        //given
        Device station = StationsFixture.evb()
                .setOpeningHours(StationsFixture.Opening.openAtWorkWeek());
        repository.save(station);
        var openingHours = StationsFixture.Opening.alwaysOpen();

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new UpdateDevice().setOpeningHours(openingHours));

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasOpeningHours(StationsFixture.Opening.alwaysOpen())
                );
    }
}
