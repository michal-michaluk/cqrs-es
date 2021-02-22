package devices.configuration.features.eggplant;

import devices.configuration.FileFixture;
import devices.configuration.features.catalogue.fileImport.eggplant.Country;
import devices.configuration.features.catalogue.fileImport.eggplant.StationsFileImportReport;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.net.URISyntaxException;

import static devices.configuration.features.eggplant.EmobilityOldPlatformClientTest.MOCK_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(EmobilityOldPlatformClient.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@TestPropertySource(properties = {
        "emobility.url=" + MOCK_URL,
        "emobility.username=fake-user",
        "emobility.password=fake-pass"
})
class EmobilityOldPlatformClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private EmobilityOldPlatformClient oldPlatformClient;

    final static String MOCK_URL = "http://someUrl";

    @Test
    public void importStationsFile() throws URISyntaxException {

        // given
        mockServer
                .expect(requestTo(new URI(MOCK_URL + "/stationconfiguration/stations")))
                .andExpect(method(POST))
                .andRespond(withStatus(CREATED)
                        .contentType(APPLICATION_JSON)
                        .body("{\n" +
                                "  \"allStationsNumber\": 3,\n" +
                                "  \"importedStations\": [\n" +
                                "    \"one\",\n" +
                                "    \"two\"\n" +
                                "  ]\n" +
                                "}"));

        // when
        StationsFileImportReport report = oldPlatformClient.importStationsFile(FileFixture.mockCsvFile(), importRequest(), "username");

        // then
        assertThat(report.getAllStationsNumber()).isEqualTo(3);
        assertThat(report.getImportedStations()).containsOnly("one", "two");
    }

    @NotNull
    private ImportRequest importRequest() {
        return new ImportRequest(Country.NORWAY, "", "", "");
    }
}
