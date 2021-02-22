package devices.configuration.features.bootNotification;

import devices.configuration.features.eggplantOutbox.EggplantService;
import devices.configuration.features.toggle.TogglesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static devices.configuration.features.bootNotification.ChargingStationManagementSystem.OLD_PLATFORM;
import static devices.configuration.features.bootNotification.protocol.EggplantProtocolName.OCPP;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.SOAP;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BootNotificationServiceTest {

    private BootNotificationService bootNotificationService;
    private StationsCatalogueRepository stationsCatalogueRepository;

    @BeforeEach
    void setupBootNotificationService() {
        stationsCatalogueRepository = mock(StationsCatalogueRepository.class);
        when(stationsCatalogueRepository.findByStationName(anyString())).thenReturn(empty());
        TogglesService togglesService = mock(TogglesService.class);
        when(togglesService.isEnabled(anyString(), anyBoolean())).thenReturn(true);
        EggplantService eggplantService = mock(EggplantService.class);
        bootNotificationService = new BootNotificationService(
                stationsCatalogueRepository, togglesService, eggplantService);
    }

    @Test
    void should_save_new_StationCatalogueEntity_with_applied_fields_after_updateBootNotificationField_was_called() {
        // given
        final String stationName = "aStation";
        final String softwareVersion = "abc.1.2.3";
        final String protocolName = OCPP.getName();
        final String protocolVersion = "1.2.0";
        BootNotificationFields bootNotificationFields =
                new BootNotificationFields(softwareVersion, protocolName, protocolVersion);
        StationsCatalogueEntity expected = stationsCatalogueEntity(stationName, softwareVersion,
                protocolName, protocolVersion, SOAP.getName(), OLD_PLATFORM.getName());

        // when
        bootNotificationService.handleBootNotificationFromEggplant(stationName, bootNotificationFields);

        // then
        verify(stationsCatalogueRepository, times(1)).save(expected);
    }

    @Test
    void should_NOT_create_new_StationCatalogueEntity_when_updateBootNotificationFieldWasCalled_with_incorrect_protocol_name() {
        // given
        final String stationName = "aStation";
        final String softwareVersion = "abc.1.2.3";
        final String invalidProtocolName = "invalid";
        final String protocolVersion = "1.2.0";
        BootNotificationFields bootNotificationFields =
                new BootNotificationFields(softwareVersion, invalidProtocolName, protocolVersion);

        // expect
        assertThrows(ResponseStatusException.class,
                () -> bootNotificationService.handleBootNotificationFromEggplant(stationName, bootNotificationFields));
        verify(stationsCatalogueRepository, never()).save(any());
    }

    @Test
    void should_NOT_create_new_StationCatalogueEntity_when_updateBootNotificationField_was_called_with_incorrect_protocol_version() {
        // given
        final String stationName = "aStation";
        final String softwareVersion = "abc.1.2.3";
        final String protocolName = OCPP.getName();
        final String invalidProtocolVersion = "invalid";
        BootNotificationFields bootNotificationFields =
                new BootNotificationFields(softwareVersion, protocolName, invalidProtocolVersion);

        // expect
        assertThrows(ResponseStatusException.class,
                () -> bootNotificationService.handleBootNotificationFromEggplant(stationName, bootNotificationFields));
        verify(stationsCatalogueRepository, never()).save(any());
    }

    private StationsCatalogueEntity stationsCatalogueEntity(String stationName, String softwareVersion,
                                                            String protocolName, String protocolVersion,
                                                            String mediaType, String csms) {
        StationsCatalogueEntity stationsCatalogueEntity = new StationsCatalogueEntity(stationName);
        stationsCatalogueEntity.setSoftwareVersion(softwareVersion);
        stationsCatalogueEntity.applyNewProtocol(protocolName, protocolVersion, mediaType);
        stationsCatalogueEntity.applyNewCsms(csms);
        return stationsCatalogueEntity;
    }
}
