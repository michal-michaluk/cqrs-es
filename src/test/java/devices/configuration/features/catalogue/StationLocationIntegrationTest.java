package devices.configuration.features.catalogue;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@IntegrationTest
class StationLocationIntegrationTest {

    @Autowired
    RequestFixture requestFixture;

    @Autowired
    StationsRepository repository;

    @Test
    void Should_get_station_without_location() {
        //given
        Station station = StationsFixture.evb()
                .setLocation(null);
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.getStation(station.getName());

        //then
        StationsResponseAssert.assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasNoLocation()
                );
    }

    @Test
    void Should_get_station_with_location() {
        //given
        Station station = StationsFixture.evb();
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.getStation(station.getName());

        //then
        StationsResponseAssert.assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasLocation(StationsFixture.Locations.rooseveltlaanInGent())
                );
    }

    @Test
    void Should_set_station_location() {
        //given
        Station station = StationsFixture.evb().setLocation(null);
        repository.save(station);
        var location = StationsFixture.Locations.rooseveltlaanInGent();

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new StationUpdate().setLocation(location));

        //then
        StationsResponseAssert.assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasLocation(StationsFixture.Locations.rooseveltlaanInGent())
                );
    }

    @Test
    void Should_normalise_2_alpha_country_iso_code_to_3_alpha() {
        //given
        Station station = StationsFixture.evb().setLocation(null);
        repository.save(station);
        var location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCountryISO("BE");

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new StationUpdate().setLocation(location));

        //then
        StationsResponseAssert.assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasLocation(StationsFixture.Locations.rooseveltlaanInGent()
                                .setCountryISO("BEL")
                        )
                );
    }

    @Test
    void Should_fail_with_invalid_location() {
        //given
        Station station = StationsFixture.evb().setLocation(null);
        repository.save(station);
        var location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCity(null)
                .setCoordinates(null);

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new StationUpdate().setLocation(location));

        //then
        StationsResponseAssert.assertThat(response)
                .isBadRequest();
    }
}
