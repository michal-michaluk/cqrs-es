package devices.configuration.features.bootNotification;

import java.util.HashMap;
import java.util.Map;

import devices.configuration.SecurityFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.IntegrationTest;
import devices.configuration.StationsFixture;
import devices.configuration.features.toggle.Toggle;
import devices.configuration.features.toggle.TogglesRepository;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BootNotificationControllerIT {

    public static final String TOGGLE_NAME = "OldPlatformBootNotification";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TogglesRepository togglesRepository;

    @Autowired
    private SecurityFixture securityFixture;

    @BeforeEach
    void setUp() {
        enableToggleForOldPlatformRestCalls();
    }

    @AfterEach
    void tearDown() {
        disableToggleForOldPlatformRestCalls();
    }

    @Test
    void should_update_station_after_calling_station_connected_handler() {
        // given
        String stationName = StationsFixture.randomStationName();
        String requestBody = "{\n"
                + "\"protocol\": {\n"
                + "      \"name\": \"ocpp\",\n"
                + "      \"mediaType\": \"json\",\n"
                + "      \"version\": \"2.0.0\"\n"
                + "    },\n"
                + "  \"csms\": \"charging-station-communication\"\n"
                + "}";

        //when
        restTemplate.put("/stations/{stationName}/connections", new HttpEntity<>(requestBody, getContentTypeJsonHeader()), Map.of("stationName", stationName));

        // then
        ResponseEntity<String> response = restTemplate.exchange("/stations/{stationName}", HttpMethod.GET, new HttpEntity<>(getAcceptJsonHeader()), String.class, stationName);

        // and
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.protocol.name")).isEqualTo("ocpp");
        assertThat(JsonPath.<String>read(response.getBody(), "$.protocol.version")).isEqualTo("2.0.0");
        assertThat(JsonPath.<String>read(response.getBody(), "$.protocol.mediaType")).isEqualTo("json");
        assertThat(JsonPath.<String>read(response.getBody(), "$.csms")).isEqualTo("charging-station-communication");
        assertThat(JsonPath.<String>read(response.getBody(), "$.stationName")).isEqualTo(stationName);
    }

    @Test
    void should_update_station_after_calling_boot_notification_handler() {
        // given
        BootNotificationFields eggplantBootNotificationFields = someBootNotificationFields();
        Map<String, String> uriParams = new HashMap<>();
        String stationName = StationsFixture.randomStationName();
        uriParams.put("stationName", stationName);
        restTemplate.exchange("/stations/{stationName}/bootNotification", HttpMethod.PUT,  new HttpEntity<>(eggplantBootNotificationFields, getContentTypeJsonHeader()), Void.class, uriParams);

        // and
        HttpEntity request = bootNotificationAcceptHeader();

        // when
        String returnedBootNotificationFields = restTemplate.exchange("/stations/{stationName}", HttpMethod.GET, request, String.class, stationName).getBody();

        // then
        compareBootNotifications(eggplantBootNotificationFields, returnedBootNotificationFields);
    }

    @Test
    void should_return_404_error_when_not_found_station() {
        // given
        String stationName = StationsFixture.randomStationName();

        // and
        HttpEntity request = bootNotificationAcceptHeader();

        // when
        ResponseEntity<String> returnedBootNotificationFields = restTemplate.exchange("/stations/{stationName}", HttpMethod.GET, request, String.class, stationName);

        // then
        assertThat(returnedBootNotificationFields.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private void enableToggleForOldPlatformRestCalls() {
        Toggle toggle = new Toggle();
        toggle.setName(TOGGLE_NAME);
        toggle.setValue(true);
        togglesRepository.save(toggle);
    }

    private void disableToggleForOldPlatformRestCalls() {
        Toggle toggle = togglesRepository.findByName(TOGGLE_NAME);
        togglesRepository.delete(toggle);
    }

    private HttpEntity bootNotificationAcceptHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, "application/vnd.vattenfall.v1.bootNotification+json");
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return new HttpEntity(headers);
    }

    private HttpHeaders getAcceptJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return headers;
    }

    private HttpHeaders getContentTypeJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return headers;
    }

    private void compareBootNotifications(BootNotificationFields bootNotificationFields, String returnedBootNotificationFields) {
        assertThat(returnedBootNotificationFields).isNotNull();
        assertThat(bootNotificationFields.getSoftwareVersion()).isEqualTo(JsonPath.read(returnedBootNotificationFields, "$.softwareVersion"));
        assertThat(bootNotificationFields.getProtocolName()).isEqualTo(JsonPath.read(returnedBootNotificationFields, "$.protocolName"));
        assertThat(bootNotificationFields.getProtocolVersion()).isEqualTo(JsonPath.read(returnedBootNotificationFields, "$.protocolVersion"));
    }

    private BootNotificationFields someBootNotificationFields() {
        return new BootNotificationFields("softwareVersion", "OCPP-AZURE", "1.6.0");
    }
}
