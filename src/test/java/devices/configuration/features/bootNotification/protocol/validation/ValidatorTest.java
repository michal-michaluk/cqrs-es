package devices.configuration.features.bootNotification.protocol.validation;

import devices.configuration.features.bootNotification.BootNotificationFields;
import devices.configuration.features.bootNotification.ProtocolDTO;
import devices.configuration.features.bootNotification.StationProtocolFromCsc;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static devices.configuration.features.bootNotification.ChargingStationManagementSystem.CHARGING_STATION_COMMUNICATION;
import static devices.configuration.features.bootNotification.ChargingStationManagementSystem.OLD_PLATFORM;
import static devices.configuration.features.bootNotification.protocol.EggplantProtocolName.*;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.JSON;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.SOAP;
import static devices.configuration.features.bootNotification.protocol.validation.Validator.invalidBootNotificationRequest;
import static devices.configuration.features.bootNotification.protocol.validation.Validator.invalidRequestFromCsc;
import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    private static Stream<Arguments> shouldValidateCscRequestArgs() {
        return Stream.of(
                //empty station name
                Arguments.of("", OCPP.getName(), "1.6.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                //csms should be csc
                Arguments.of("aStation", OCPP.getName(), "1.6.0", JSON.getName(), OLD_PLATFORM.getName(), true),
                //incorrect csms
                Arguments.of("aStation", OCPP.getName(), "1.6.0", JSON.getName(), "wrong csms", true),
                //media type should be JSON
                Arguments.of("aStation", OCPP.getName(), "1.6.0", SOAP.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                //incorrect protocol for CSC - OCPP_J
                Arguments.of("aStation", OCPP_J.getName(), "1.6.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                //incorrect protocol for CSC - OCPP_AZURE
                Arguments.of("aStation", OCPP_AZURE.getName(), "1.6.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                //incorrect protocol for CSC - 1.2.0
                Arguments.of("aStation", OCPP.getName(), "1.2.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                // station name is null, protocol is not supported by CSC
                Arguments.of(null, OCPP_AZURE.getName(), "2.0.1+", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                // station name is blank, protocol is not supported by CSC
                Arguments.of("", OCPP_AZURE.getName(), "2.0.1+", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                // not all protocol fields are provided - in CSC it is mandatory
                Arguments.of("aStation", null, "2.0.1+", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                // not all protocol fields are provided - in CSC it is mandatory
                Arguments.of("aStation", OCPP_AZURE.getName(), null, JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), true),
                // all below is OK
                Arguments.of("aStation", OCPP.getName(), "1.6.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), false),
                Arguments.of("aStation", OCPP.getName(), "1.6.1", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), false),
                Arguments.of("aStation", OCPP.getName(), "1.6.456abc", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), false),
                Arguments.of("aStation", OCPP.getName(), "2.0.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), false),
                Arguments.of("aStation", OCPP.getName(), "2.0", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), false),
                Arguments.of("aStation", OCPP.getName(), "2.0.8r", JSON.getName(), CHARGING_STATION_COMMUNICATION.getName(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("shouldValidateCscRequestArgs")
    void shouldValidateCscRequest(String stationName, String protocolName, String protocolVersion, String mediaType,
                                  String csms, boolean validationResult) {
        // given
        StationProtocolFromCsc stationProtocolFromCsc = new StationProtocolFromCsc(
                new ProtocolDTO(protocolName, protocolVersion, mediaType),
                csms);

        // when
        final boolean result = invalidRequestFromCsc(stationName, stationProtocolFromCsc);

        // expect
        assertThat(validationResult).isEqualTo(result);
    }


    private static Stream<Arguments> shouldValidateBootNotificationRequestArgs() {
        return Stream.of(
                // empty station name
                Arguments.of("", OCPP.getName(), "1.5.0", true),
                // wrong protocol version
                Arguments.of("aStation", OCPP.getName(), "2.0.0", true),
                // incorrect protocol version
                Arguments.of("aStation", OCPP.getName(), "incorrect", true),
                // wrong protocol version
                Arguments.of("aStation", OCPP_J.getName(), "1.2.0", true),
                // wrong protocol version
                Arguments.of("aStation", OCPP_J.getName(), "1.5.0", true),
                // wrong protocol version
                Arguments.of("aStation", OCPP_J.getName(), "2.0.0", true),
                // incorrect protocol version
                Arguments.of("aStation", OCPP_J.getName(), "incorrect", true),
                // incorrect protocol version
                Arguments.of("aStation", OCPP_AZURE.getName(), "1.2.0", true),
                // incorrect protocol version
                Arguments.of("aStation", OCPP_AZURE.getName(), "1.5.0", true),
                // incorrect protocol version
                Arguments.of("aStation", OCPP_AZURE.getName(), "incorrect", true),
                // all below are OK
                Arguments.of("aStation", OCPP.getName(), "1.2.0", false),
                Arguments.of("aStation", OCPP.getName(), "1.2", false),
                Arguments.of("aStation", OCPP.getName(), "1.2.abc", false),
                Arguments.of("aStation", OCPP.getName(), "1.2.0+", false),

                Arguments.of("aStation", OCPP.getName(), "1.5.0", false),
                Arguments.of("aStation", OCPP.getName(), "1.5", false),
                Arguments.of("aStation", OCPP.getName(), "1.5.abc", false),
                Arguments.of("aStation", OCPP.getName(), "1.5.0+", false),

                Arguments.of("aStation", OCPP.getName(), "1.6.0", false),
                Arguments.of("aStation", OCPP.getName(), "1.6", false),
                Arguments.of("aStation", OCPP.getName(), "1.6.abc", false),
                Arguments.of("aStation", OCPP.getName(), "1.6.0+", false),

                Arguments.of("aStation", OCPP_J.getName(), "1.6.0", false),
                Arguments.of("aStation", OCPP_J.getName(), "1.6", false),
                Arguments.of("aStation", OCPP_J.getName(), "1.6.abc", false),
                Arguments.of("aStation", OCPP_J.getName(), "1.6.0+", false),

                Arguments.of("aStation", OCPP_AZURE.getName(), "1.6.0", false),
                Arguments.of("aStation", OCPP_AZURE.getName(), "1.6", false),
                Arguments.of("aStation", OCPP_AZURE.getName(), "1.6.abc", false),
                Arguments.of("aStation", OCPP_AZURE.getName(), "1.6.0+", false),

                Arguments.of("aStation", OCPP_AZURE.getName(), "2.0.0", false),
                Arguments.of("aStation", OCPP_AZURE.getName(), "2.0", false),
                Arguments.of("aStation", OCPP_AZURE.getName(), "2.0.abc", false),
                Arguments.of("aStation", OCPP_AZURE.getName(), "2.0.1+", false)
        );
    }

    @ParameterizedTest
    @MethodSource("shouldValidateBootNotificationRequestArgs")
    void shouldValidateBootNotificationRequest(String stationName, String protocolName, String protocolVersion,
                                               boolean validationResult) {
        // given
        BootNotificationFields bootNotificationFields = new BootNotificationFields(
                "softwareVersion", protocolName, protocolVersion
        );

        // when
        final boolean result = invalidBootNotificationRequest(stationName, bootNotificationFields);

        // expect
        assertThat(validationResult).isEqualTo(result);
    }
}
