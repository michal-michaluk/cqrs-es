package devices.configuration.features.toggle;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.IntegrationTest;
import devices.configuration.SecurityFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IntegrationTest
class TogglesIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    ToggleFixture toggleFixture;

    @Autowired
    SecurityFixture securityFixture;

    @Test
    public void ShouldReturnAllToggles() {
        // given
        Toggle toggle = toggleFixture.someToggleFromDb();

        // when
        ResponseEntity<List> response = testRestTemplate.exchange(
                "/toggles/", HttpMethod.GET, new HttpEntity<>(jsonHeaders()), List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readItems(response, "$.*.name")).contains(toggle.name);
        assertThat(readItemsAsBooleans(response, "$.*.value")).contains(toggle.value);
    }

    private List<String> readItems(ResponseEntity<List> stationsListResponse, String s) {
        return JsonPath.read(stationsListResponse.getBody(), s);
    }

    private List<Boolean> readItemsAsBooleans(ResponseEntity<List> stationsListResponse, String s) {
        return JsonPath.read(stationsListResponse.getBody(), s);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return headers;
    }
}
