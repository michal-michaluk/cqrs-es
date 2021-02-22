package devices.configuration.legacy.stationImport;

import devices.configuration.features.catalogue.Station;
import devices.configuration.features.catalogue.StationImageProperties;
import devices.configuration.features.catalogue.StationsFixture;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.legacy.LocationFixing;
import devices.configuration.legacy.StationLocationValidator;
import devices.configuration.legacy.stationImport.StationsImporter.UpdateScope;
import devices.configuration.legacy.stationImport.report.ImportReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static devices.configuration.legacy.stationImport.LocationViewFixture.location;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class StationsImporterTest {

    private static final String STATION_NAME = "stationName";

    @Mock
    private ImportReport report;

    @Captor
    private ArgumentCaptor<Station> entityCaptor;

    @Mock
    private StationsRepository stationsRepository;

    @Mock
    private LocationFixing locationFixing;

    @Mock
    private StationLocationValidator locationValidator;

    @Mock
    private StationImageProperties stationImageProperties;

    @InjectMocks
    private StationsImporter importer;


    @BeforeEach
    void setup() {
        initMocks(this);

        when(stationImageProperties.getId()).thenReturn("defaultId");
    }

    @Test
    void Should_import_location_for_station_not_found_in_database() {
        // given
        stationNotFoundInDatabase();
        var station = stationView(STATION_NAME, validLocation());

        // when
        whenImported(station);

        // then
        assertLocationSaved(captureStation(), station);
    }

    @Test
    void Should_import_stations_without_location_for_station_not_found_in_database_when_location_invalid_in_eggplant() {
        // given
        stationNotFoundInDatabase();
        var station = stationView(STATION_NAME, invalidLocation());

        // when
        whenImported(station);

        // then
        Station saved = captureStation();
        assertStationSaved(saved, station);
        assertThat(saved.getLocation()).isNull();
    }

    @Test
    void Should_overwrite_location_when_station_in_database_has_one_already() {
        // given
        stationFoundInDatabase(STATION_NAME, validLocation());
        var station = stationView(STATION_NAME, validLocation());

        // when
        whenImported(station);

        // then
        assertLocationSaved(captureStation(), station);
    }

    @Test
    void Should_save_location_when_station_in_database_has_it_null() {
        // given
        stationFoundInDatabase(STATION_NAME, null);
        var station = stationView(STATION_NAME, validLocation());

        // when
        whenImported(station);

        // then
        assertLocationSaved(captureStation(), station);
    }

    @Test
    void Should_save_location_when_station_in_database_has_invalid_location() {
        // given
        stationFoundInDatabase(STATION_NAME, invalidLocation());
        var station = stationView(STATION_NAME, validLocation());

        // when
        whenImported(station);

        // then
        assertLocationSaved(captureStation(), station);
    }

    private void whenImported(StationView.ChargingStation station) {
        importer.importStations(Set.of(UpdateScope.LOCATION), List.of(station), report);
    }

    private void assertLocationSaved(Station station, StationView.ChargingStation stationView) {
        assertThat(station.getName()).isEqualTo(stationView.getName());
        assertThat(station.getLocation())
                .isNotNull()
                .isEqualTo(stationView.getLocation().toLocation());
        assertValidEntity(station);
    }

    private void assertStationSaved(Station station, StationView.ChargingStation stationView) {
        assertThat(station.getName()).isEqualTo(stationView.getName());
        assertValidEntity(station);
    }

    private void assertValidEntity(Station station) {
        var violations = buildDefaultValidatorFactory().getValidator().validate(station);
        assertThat(violations).isEmpty();
    }

    private Station captureStation() {
        verify(stationsRepository).save(entityCaptor.capture());
        return entityCaptor.getValue();
    }

    private void stationNotFoundInDatabase() {
        doReturn(empty()).when(stationsRepository).findByName(any());
    }

    private void stationFoundInDatabase(String name, Location location) {
        var evb = StationsFixture.evb(name)
                .setLocation(location);
        doReturn(of(evb)).when(stationsRepository).findByName(eq(name));
    }

    private StationView.ChargingStation stationView(String stationName, Location location) {
        return StationViewFixture.stationViewWithLocation(stationName, location);
    }

    private Location validLocation() {
        Location location = LocationViewFixture.location();
        doReturn(LocationFixing.Status.notTouched())
                .when(locationFixing).process(eq(location));
        return location;
    }

    private Location invalidLocation() {
        Location location = LocationViewFixture.location();
        Throwable exception = new Throwable("test exception");
        doReturn(LocationFixing.Status.failure(exception))
                .when(locationFixing).process(eq(location));
        doReturn("some violations").when(locationValidator).issues(any(), eq(location));
        return location;
    }

    private void assertNothingSaved() {
        verify(stationsRepository, never()).save(any());
    }
}
