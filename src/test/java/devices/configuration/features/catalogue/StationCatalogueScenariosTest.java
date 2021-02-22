package devices.configuration.features.catalogue;

import devices.configuration.IntegrationTest;
import devices.configuration.outbox.OutgoingEventsTestListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static devices.configuration.features.catalogue.StationsResponseAssert.assertThat;
import static devices.configuration.outbox.OutgoingEventsTestListener.event;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class StationCatalogueScenariosTest {

    @Autowired
    RequestFixture requestFixture;

    @Autowired
    StationImageProperties stationImageProperties;

    @Autowired
    StationsRepository repository;

    @Autowired
    OutgoingEventsTestListener events;

    private final String stationName = "one-station-name";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        events.clear();
    }

    @Test
    void Should_provide_station_after_import() {
        //given
        importStation();

        //when
        ResponseEntity<String> response = requestFixture.getStation(stationName);

        //then
        assertThat(response).isOK()
                .hasStationAsBody(station -> station
                        .hasStationName(stationName)
                        .hasPhysicalReference("EVB-123234")
                        .hasNumberOfOutlets(2)
                        .hasMaxNumberOfOutlets(20)
                        .hasVendor("EVBOX")
                        .hasProduct("ELVI")
                        .hasProductDetails("ELVI273")
                        .hasColor("Gray")
                        .hasComment1("looks like a shoebox")
                        .hasComment2("just some free fields plhed")
                        .hasCapabilitiesAvailableOcppVersions("1.5/1.6")
                        .hasCapabilitiesDebitPayment(false)
                        .hasCapabilitiesReservation(true)
                        .hasCapabilitiesCreditPayment(false)
                        .hasCapabilitiesRfidReader(true)
                        .hasCapabilitiesRemoteStart(true)
                        .hasCapabilitiesScnDlb(true)
                        .hasCapabilitiesTokenGrouping(true)
                        .hasCapabilitiesSmartCharging(true)
                        .hasCapabilitiesUnlock(true)
                        .hasCapabilitiesDc(false)
                        .hasAddedOnNotNull()
                        .hasImageId(stationImageProperties.getDefaultImageId().getId())
                        .hasNoLocation()
                        .hasDefaultOpeningHours()
                );
    }

    @Test
    void Should_update_location_after_import() {
        //given
        importStation();

        //and
        requestFixture.updateStation(stationName, new StationUpdate().setLocation(StationsFixture.Locations.rooseveltlaanInGent()));

        //when
        ResponseEntity<String> response = requestFixture.getStation(stationName);

        //then
        assertThat(response).isOK()
                .hasStationAsBody(station -> station
                        .hasStationName(stationName)
                        .hasLocation(StationsFixture.Locations.rooseveltlaanInGent())
                );

        //and
        events.hasExactly(1, event()
                .ofTypeStationLocationUpdated()
                .withStationName(stationName)
        );
    }

    @Test
    void Should_preserve_location_after_reimport() {
        //given
        importStation();

        //and
        requestFixture.updateStation(stationName, new StationUpdate().setLocation(StationsFixture.Locations.rooseveltlaanInGent()));

        //and
        ResponseEntity<String> response = requestFixture.getStation(stationName);

        //when
        importStation();

        //then
        assertThat(response).isOK()
                .hasStationAsBody(station -> station
                        .hasStationName(stationName)
                        .hasLocation(StationsFixture.Locations.rooseveltlaanInGent())
                );

        //and
        events.hasExactly(1, event()
                .ofTypeStationLocationUpdated()
                .withStationName(stationName)
        );
    }

    @Test
    void Should_update_opening_hours_after_import() {
        //given
        importStation();

        //and
        requestFixture.updateStation(stationName, new StationUpdate().setOpeningHours(StationsFixture.Opening.openAtWorkWeek()));

        //when
        ResponseEntity<String> response = requestFixture.getStation(stationName);

        //then
        assertThat(response).isOK()
                .hasStationAsBody(station -> station
                        .hasStationName(stationName)
                        .hasOpeningHours(StationsFixture.Opening.openAtWorkWeek())
                );
    }

    private void importStation() {
        ResponseEntity<String> requestEntity = requestFixture.importStations(getTestFile());
        assertThat(requestEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private FileSystemResource getTestFile() {
        return new FileSystemResource("./src/test/resources/one_station.csv");
    }
}
