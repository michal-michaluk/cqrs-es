package devices.configuration.legacy;

import devices.configuration.IntegrationTest;
import devices.configuration.features.Toggles;
import devices.configuration.features.catalogue.StationUpdate;
import devices.configuration.features.catalogue.StationsFixture;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.toggle.TogglesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static devices.configuration.legacy.StationLocationValidator.UNKNOWN;
import static devices.configuration.legacy.StationLocationValidator.DEFAULT_POSTAL_CODE;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class LocationSynchronisationServiceTest {

    @Autowired
    LocationSynchronisationService service;
    @Autowired
    StationsRepository repository;
    @Autowired
    TogglesService toggles;

    private String stationName;

    @BeforeEach
    void setUp() {
        stationName = UUID.randomUUID().toString();
    }

    @Test
    void Should_change_station_location_after_correct_updated() {
        //given
        givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam());

        //then
        thenLocation(StationsFixture.Locations.dusartstraatInAmsterdam());
    }

    @Test
    void Should_ignore_event_with_null_coordinates() {
        //given
        givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam()
                .setCoordinates(null));

        //then
        thenLocation(StationsFixture.Locations.rooseveltlaanInGent());
    }

    @Test
    void Should_ignore_event_with_0000AA_postal_code() {
        //given
        givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam()
                .setPostalCode(DEFAULT_POSTAL_CODE));

        //then
        thenLocation(StationsFixture.Locations.rooseveltlaanInGent());
    }

    @Test
    void Should_ignore_event_with_UNKNOWN_postal_code() {
        //given
        givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam()
                .setPostalCode(UNKNOWN));

        //then
        thenLocation(StationsFixture.Locations.rooseveltlaanInGent());
    }

    @Test
    void Should_ignore_event_with_UNKNOWN_city() {
        //given
        givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam()
                .setCity(UNKNOWN));

        //then
        thenLocation(StationsFixture.Locations.rooseveltlaanInGent());
    }

    @Test
    void Should_ignore_event_with_UNKNOWN_street() {
        //given
        givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam()
                .setStreet(UNKNOWN));

        //then
        thenLocation(StationsFixture.Locations.rooseveltlaanInGent());
    }

    @Test
    void Should_ignore_event_when_toggle_off() {
        toggleDisabled(() -> {
            //given
            givenLocation(StationsFixture.Locations.rooseveltlaanInGent());

            //when
            whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam());

            //then
            thenLocation(StationsFixture.Locations.rooseveltlaanInGent());
        });
    }

    @Test
    void Should_create_new_station() {
        //given
        noStation();

        //when
        whenLocationUpdated(StationsFixture.Locations.dusartstraatInAmsterdam());

        //then
        thenLocation(StationsFixture.Locations.dusartstraatInAmsterdam());
    }

    private void givenLocation(Location location) {
        repository.save(StationsFixture.evb(stationName)
                .setLocation(location)
        );
    }

    private void noStation() {
        assertThat(repository.findByName(stationName)).isEmpty();
    }

    private void whenLocationUpdated(Location location) {
        service.syncStationLocation(
                List.of(stationName),
                new StationUpdate().setLocation(location.setUpdate(false)),
                "json not provided for junit test"
        );
    }

    private void toggleDisabled(Runnable runnable) {
        try {
            toggles.disableToggle(Toggles.SYNC_LOCATIONUPDATED_FROM_EGGPLANT);
            runnable.run();
        } finally {
            toggles.enableToggle(Toggles.SYNC_LOCATIONUPDATED_FROM_EGGPLANT);
        }
    }

    private void thenLocation(Location location) {
        assertThat(repository.findByName(stationName))
                .hasValueSatisfying(s ->
                        assertThat(s.getLocation())
                                .isEqualTo(location)
                );
    }
}
