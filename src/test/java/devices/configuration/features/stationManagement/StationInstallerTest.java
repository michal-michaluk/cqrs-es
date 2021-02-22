package devices.configuration.features.stationManagement;

import devices.configuration.SecurityFixture;
import devices.configuration.features.catalogue.Ownership;
import devices.configuration.features.catalogue.StationUpdate;
import devices.configuration.features.catalogue.StationsService;
import devices.configuration.features.stationManagement.StationManagementService.EventToggleDisabledException;
import devices.configuration.features.stationManagement.installation.InstallStation;
import devices.configuration.features.stationManagement.installation.StationInstallationRegistry;
import devices.configuration.features.stationManagement.installation.StationInstaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
class StationInstallerTest {

    @Mock
    private StationManagementService stationManagement;

    @Mock
    private StationInstallationRegistry registry;

    @Mock
    private StationsService stationsService;

    @InjectMocks
    private StationInstaller installer;

    @BeforeEach
    void setup() {
        initMocks(this);
    }

    private String STATION = "station";
    private String CPO = "cpo";
    private String LCP = "lcp";
    private String USER = "someUser";

    @Test
    void shouldRegisterButNotFullyInstallStationWhenLcpIsNull() {
        // given
        var request = new InstallStation(CPO, null);
        var authentication = SecurityFixture.cpoAuth(USER, CPO);

        // when
        installer.install(STATION, request, authentication);

        // then
        verify(stationManagement).generateStationCreatedEvent(STATION);
        verify(stationManagement).generateStationCpoUpdated(STATION, CPO);
        verify(registry).deleteAllByStationName(STATION);
        verify(registry).save(any());

        verifyNoInteractions(stationsService);
        verify(stationManagement, never()).generateStationInstalledToLcp(eq(STATION), any());
    }

    @Test
    void shouldFullyInstallStationWhenLcpProvided() {
        // given
        var request = new InstallStation(CPO, LCP);
        var authentication = SecurityFixture.cpoAuth(USER, CPO);

        // when
        installer.install(STATION, request, authentication);

        // then
        verify(stationManagement).generateStationCreatedEvent(STATION);
        verify(stationManagement).generateStationCpoUpdated(STATION, CPO);
        verify(registry).deleteAllByStationName(STATION);
        verify(registry).save(any());

        verify(stationsService).updateStation(STATION, new StationUpdate().setOwnership(new Ownership(CPO, LCP)));
        verify(stationManagement).generateStationInstalledToLcp(STATION, LCP);
        verify(registry).deleteById(STATION);
    }

    @Test
    void shouldThrowWhenStationCreatedToggleDisabled() {
        // given
        doThrow(new EventToggleDisabledException("reason"))
                .when(stationManagement)
                .generateStationCreatedEvent(any());

        var request = new InstallStation(CPO, null);
        var authentication = SecurityFixture.cpoAuth(USER, CPO);

        // expected
        assertThrows(ResponseStatusException.class, () ->
                installer.install(STATION,
                        request,
                        authentication));

        verify(stationManagement, never()).generateStationCpoUpdated(eq(STATION), any());
        verify(stationManagement, never()).generateStationInstalledToLcp(eq(STATION), any());
        verify(registry).deleteAllByStationName(STATION);
        verify(registry).save(any());
        verifyNoInteractions(stationsService);
    }
}