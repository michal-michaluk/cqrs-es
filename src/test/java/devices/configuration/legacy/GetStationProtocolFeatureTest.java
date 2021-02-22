package devices.configuration.legacy;

import devices.configuration.IntegrationTest;
import devices.configuration.features.Toggles;
import devices.configuration.features.bootNotification.*;
import devices.configuration.features.toggle.ToggleFixture;
import devices.configuration.StationsFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
class GetStationProtocolFeatureTest {

    @Autowired
    BootNotificationService bootNotificationService;

    @Autowired
    StationsCatalogueRepository stationsCatalogueRepository;

    @Autowired
    ToggleFixture toggleFixture;

    @BeforeEach
    void setUp() {
        stationsCatalogueRepository.deleteAll();
        toggleFixture.setToggleValue(Toggles.OLD_PLATFORM_BOOT_NOTIFICATION, true);
    }

    private static Stream<Arguments> should_return_station_protocol_args() {
        return Stream.of(
                Arguments.of("1.2.0", "OCPP", new ProtocolDTO("ocpp", "1.2.0", "soap")),
                Arguments.of("1.5.0", "OCPP", new ProtocolDTO("ocpp", "1.5.0", "soap")),
                Arguments.of("1.6.0", "OCPP", new ProtocolDTO("ocpp", "1.6.0", "soap")),
                Arguments.of("1.6.0", "OCPP-J", new ProtocolDTO("ocpp", "1.6.0", "json")),
                Arguments.of("1.6.0", "OCPP-AZURE", new ProtocolDTO("ocpp", "1.6.0", "json")),
                Arguments.of("2.0.0", "OCPP-AZURE", new ProtocolDTO("ocpp", "2.0.0", "json")));
    }

    @ParameterizedTest
    @MethodSource("should_return_station_protocol_args")
    void Should_return_station_protocol(String xmlRpcVersion, String xmlRpcVendor, ProtocolDTO expectedSupportedProtocol) {
        // given
        String station = StationsFixture.randomStationName();
        final BootNotificationFields bootNotificationFields =
                new BootNotificationFields("", xmlRpcVendor, xmlRpcVersion);

        // when
        bootNotificationService.handleBootNotificationFromEggplant(station, bootNotificationFields);
        StationProtocolDTO details = bootNotificationService.getStationDetails(station);

        // then
        assertEquals(expectedSupportedProtocol, details.getProtocol());
    }

    @Test
    void Should_return_empty_protocol_when_given_protocol_fields_are_nulls() {
        // given
        String station = StationsFixture.randomStationName();
        ProtocolDTO EMPTY_PROTOCOL = new ProtocolDTO(null, null, null);

        final BootNotificationFields bootNotificationFields =
                new BootNotificationFields("", null, null);

        // when
        bootNotificationService.handleBootNotificationFromEggplant(station, bootNotificationFields);
        StationProtocolDTO details = bootNotificationService.getStationDetails(station);

        // then
        assertEquals(EMPTY_PROTOCOL, details.getProtocol());
    }

    private static Stream<Arguments> should_not_create_station_when_protocol_is_not_supported_args() {
        return Stream.of(
                Arguments.of("wrong_version", null),
                Arguments.of("1.5.0", null),
                Arguments.of(null, "wrong vendor"),
                Arguments.of(null, "OCCP")
        );
    }
    @ParameterizedTest
    @MethodSource("should_not_create_station_when_protocol_is_not_supported_args")
    void should_not_create_station_when_protocol_is_not_supported(String protocolVersion, String protocolVendor) {
        // given
        String station = StationsFixture.randomStationName();

        final BootNotificationFields bootNotificationFields =
                new BootNotificationFields("", protocolVendor, protocolVersion);

        // expect
        assertThrows(ResponseStatusException.class,
                () -> bootNotificationService.handleBootNotificationFromEggplant(station, bootNotificationFields));
        assertThrows(ResponseStatusException.class, () -> bootNotificationService.getStationDetails(station));
    }

    @Test
    void Should_return_empty_optional_when_cannot_find_station_with_given_name() {
        // expect
        Assertions.assertThrows(ResponseStatusException.class, () ->
                bootNotificationService.getStationDetails(StationsFixture.randomStationName())
        );
    }
}
