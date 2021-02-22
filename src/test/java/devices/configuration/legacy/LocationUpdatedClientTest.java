package devices.configuration.legacy;

import devices.configuration.features.catalogue.StationsFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(LocationUpdatedClient.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@TestPropertySource(properties = {
        "emobility.url=http://fake.emob",
        "emobility.username=fake-user",
        "emobility.password=fake-pass"
})
class LocationUpdatedClientTest {

    @Value("${emobility.url}")
    String url;

    @Autowired
    private LocationUpdatedClient client;

    @Autowired
    private MockRestServiceServer server;

    private String station = UUID.randomUUID().toString();

    @Test
    void Should_send_updated_Station_Location() {
        expectRequest()
                .andExpect(content().json(expectedJson()))
                .andRespond(withSuccess());

        client.sendLocationUpdated(station, givenLocation());
        server.verify();
    }

    @Test
    void Should_repeat_on_fail() {
        expectRequest()
                .andExpect(content().json(expectedJson()))
                .andRespond(withServerError());

        expectRequest()
                .andExpect(content().json(expectedJson()))
                .andRespond(withSuccess());

        client.sendLocationUpdated(station, givenLocation());
        server.verify();
    }

    @Test
    void Should_process_update_events() {
        expectRequest()
                .andExpect(content().json(expectedJson()))
                .andRespond(withSuccess());
        client.handle(new StationLocationUpdated(station,
                StationLocation.from(StationsFixture.Locations.dusartstraatInAmsterdam()), true));
        server.verify();
    }

    @Test
    void Should_ignore_sync_events() {
        client.handle(new StationLocationUpdated(station,
                StationLocation.from(StationsFixture.Locations.dusartstraatInAmsterdam()), false));
        server.verify();
    }

    private ResponseActions expectRequest() {
        return server.expect(requestTo(url + "/stationconfiguration/locations/" + station));
    }

    private LocationUpdatedClient.LocationUpdatedFields givenLocation() {
        return new LocationUpdatedClient.LocationUpdatedFields(new StationLocation(
                "Amsterdam",
                "3",
                "Dusartstraat",
                "1072HS",
                "NLD",
                new BigDecimal("4.809561"),
                new BigDecimal("52.352206")
        ));
    }

    private String expectedJson() {
        return "{ \n"
                + "  \"location\" : {\n"
                + "    \"city\" : \"Amsterdam\",\n"
                + "    \"houseNumber\" : \"3\",\n"
                + "    \"street\" : \"Dusartstraat\",\n"
                + "    \"zipcode\" : \"1072HS\",\n"
                + "    \"country\" : \"NLD\",\n"
                + "    \"longitude\" : 4.809561,\n"
                + "    \"latitude\" : 52.352206\n"
                + "  }\n"
                + "}";
    }
}
