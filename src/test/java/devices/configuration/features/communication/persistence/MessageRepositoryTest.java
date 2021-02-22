package devices.configuration.features.communication.persistence;

import devices.configuration.IntegrationTest;
import devices.configuration.StationsFixture;
import devices.configuration.features.communication.Endpoint;
import devices.configuration.features.communication.UnifiedMessagesFixture;
import devices.configuration.features.communication.ocpp16.Ocpp16MessagesFixture;
import devices.configuration.features.communication.ocpp20.Ocpp20MessagesFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static devices.configuration.features.communication.ocpp16.Ocpp16MessagesFixture.json;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class MessageRepositoryTest {

    Ocpp16MessagesFixture ocpp16 = new Ocpp16MessagesFixture();
    Ocpp20MessagesFixture ocpp20 = new Ocpp20MessagesFixture();

    @Autowired
    MessageRepository repository;

    @Test
    void Should_persist_BootNotification_1_6() {
        var message = Ocpp16MessagesFixture.json(ocpp16.bootNotification());
        repository.save(StationsFixture.randomStationName(), Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, message);
    }

    @Test
    void Should_load_persisted_BootNotification_1_6() {
        var message = Ocpp16MessagesFixture.json(ocpp16.bootNotification());
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, message);

        var boot = repository.get(station, Ocpp16MessagesFixture.BOOT_TYPE);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(ocpp16.bootNotification());
    }

    @Test
    void Should_persist_BootNotification_2_0() {
        var message = Ocpp16MessagesFixture.json(ocpp20.bootNotification());
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, message);
    }

    @Test
    void Should_load_persisted_BootNotification_2_0() {
        var message = Ocpp16MessagesFixture.json(ocpp20.bootNotification());
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, message);

        var boot = repository.get(station, Ocpp20MessagesFixture.BOOT_TYPE);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(ocpp20.bootNotification());
    }

    @Test
    void Should_load_last_persisted_message() {
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp16.bootNotification("1.1")));
        repository.save(station, Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp16.bootNotification("1.2")));
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp20.bootNotification("2.0")));
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp20.bootNotification("2.1")));

        var boot = repository.get(station, Ocpp20MessagesFixture.BOOT_TYPE);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(ocpp20.bootNotification("2.1"));
    }

    @Test
    void Should_load_last_StationDetails_from_1_6() {
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp16.bootNotification("1.1")));
        repository.save(station, Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp16.bootNotification("1.2")));

        var boot = repository.getStationDetails(station);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(UnifiedMessagesFixture.stationDetailsGaroWithMeter("1.2"));
    }

    @Test
    void Should_load_last_StationDetails_from_1_6_only_nulls() {
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP16J, Ocpp16MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp16.bootNotificationWithOnlyNulls()));

        var boot = repository.getStationDetails(station);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(UnifiedMessagesFixture.stationDetailsWithOnlyNulls());
    }

    @Test
    void Should_load_last_StationDetails_from_2_0() {
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp20.bootNotification("2.0")));
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp20.bootNotification("2.1")));

        var boot = repository.getStationDetails(station);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(UnifiedMessagesFixture.stationDetailsChargePoint("2.1"));
    }

    @Test
    void Should_load_last_StationDetails_from_2_0_null_station() {
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp20.bootNotificationWithNullStation()));

        var boot = repository.getStationDetails(station);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(UnifiedMessagesFixture.stationDetailsWithOnlyNulls());
    }

    @Test
    void Should_load_last_StationDetails_from_2_0_null_modem() {
        String station = StationsFixture.randomStationName();
        repository.save(station, Endpoint.OCPP20J, Ocpp20MessagesFixture.BOOT_TYPE, Ocpp16MessagesFixture.json(ocpp20.bootNotificationWithNullModem("1.0")));

        var boot = repository.getStationDetails(station);

        assertThat(boot)
                .isNotEmpty()
                .hasValue(UnifiedMessagesFixture.stationDetailsWithoutModemAndMeter("1.0"));
    }

    @Test
    void Sould_return_empty_optional_if_there_were_no_boot_notifications() {
        //given + no bootnotifications were saved
        String station = StationsFixture.randomStationName();

        // when
        var boot = repository.getStationDetails(station);

        // then
        assertThat(boot)
                .isEmpty();
    }
}
