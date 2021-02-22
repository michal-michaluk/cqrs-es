package devices.configuration.features.stationManagement.installation;

import devices.configuration.IntegrationTest;
import devices.configuration.SecurityFixture;
import devices.configuration.features.Toggles;
import devices.configuration.features.toggle.ToggleFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static devices.configuration.SecurityFixture.DEFAULT_CPO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@IntegrationTest
class StationInstallationControllerIntegrationTest {

    @Autowired
    private ToggleFixture toggles;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SecurityFixture securityFixture;

    private static final String STATION = "station";

    @Test
    void shouldCallStationInstallation() {
        // given
        givenAllTogglesEnabled();

        String json = ownership();

        // when
        var response = restTemplate.exchange("/registration/stations/" + STATION , PUT, new HttpEntity<>(json, headersWithAuth()), String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void shouldReturn403ForStationCreatedEventToggleDisabled() {
        // given
        toggles.setToggleValue(Toggles.CAN_SEND_STATION_CREATED, false);

        String json = ownership();

        // when
        var response = restTemplate.exchange("/registration/stations/" + STATION, PUT, new HttpEntity<>(json, headersWithAuth()), String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    private String ownership() {
        return "{" +
                "    \"cpo\": \"" + DEFAULT_CPO + "\"," +
                "    \"lcp\": \"LCP someUser\"" +
                "}";
    }

    private void givenAllTogglesEnabled() {
        toggles.enable(Toggles.CAN_SEND_STATION_CREATED);
        toggles.enable(Toggles.CAN_SEND_STATION_CPO_UPDATED);
        toggles.enable(Toggles.CAN_SEND_STATION_INSTALLED_TO_LCP);
    }

    private MultiValueMap<String, String> headersWithAuth() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(AUTHORIZATION, securityFixture.operatorToken());
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return headers;
    }
}