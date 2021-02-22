package devices.configuration.features.catalogue;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static devices.configuration.features.catalogue.StationsResponseAssert.assertThat;

@IntegrationTest
class StationSettingsIntegrationTest {

    @Autowired
    RequestFixture requestFixture;

    @Autowired
    StationsRepository repository;

    @Test
    void Should_get_settings() {
        //given
        Station station = StationsFixture.evb()
                .setSettings(Settings.defaultSettings());
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.getStation(station.getName());

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasSettings(Settings.defaultSettings())
                );
    }

    @Test
    void Should_get_station_with_specific_settings() {
        //given
        Settings settings = Settings.defaultSettings().toBuilder()
                .showOnMap(true)
                .publicAccess(true)
                .build();

        Station station = StationsFixture.evb()
                .setSettings(settings);
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.getStation(station.getName());

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasSettings(settings)
                );
    }

    @Test
    void Should_set_station_settings() {
        //given
        Station station = StationsFixture.evb()
                .setSettings(Settings.defaultSettings());
        repository.save(station);

        Settings settings = Settings.defaultSettings().toBuilder()
                .showOnMap(true)
                .publicAccess(true)
                .build();

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new StationUpdate().setSettings(settings));

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasSettings(settings)
                );
    }

    @Test
    void Should_not_be_able_to_reset_station_settings() {
        //given
        Settings initialSetting = Settings.defaultSettings().toBuilder()
                .showOnMap(true)
                .publicAccess(true)
                .build();

        Station station = StationsFixture.evb()
                .setSettings(initialSetting);
        repository.save(station);

        //when
        ResponseEntity<String> response = requestFixture.updateStation(station.getName(), new StationUpdate().setSettings(Settings.defaultSettings().toBuilder()
                .autoStart(null)
                .remoteControl(null)
                .billing(null)
                .reimbursement(null)
                .showOnMap(null)
                .publicAccess(null)
                .build()));

        //then
        assertThat(response).isOK()
                .hasStationAsBody(actual -> actual
                        .hasStationName(station.getName())
                        .hasSettings(initialSetting)
                );
    }
}
