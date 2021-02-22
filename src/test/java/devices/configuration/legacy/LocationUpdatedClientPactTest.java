package devices.configuration.legacy;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import devices.configuration.IntegrationTest;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "old-platform", port = "8888")
@IntegrationTest
@TestPropertySource(properties = {
        "emobility.url=http://localhost:8888",
        "emobility.username=fake-user",
        "emobility.password=fake-pass"
})
public class LocationUpdatedClientPactTest {

    @Autowired
    private LocationUpdatedClient client;

    @Pact(provider = "old-platform", consumer = "station-configuration")
    RequestResponsePact notExistingStation(PactDslWithProvider builder) {
        return builder
                .given("station 'fake-station' not exists")
                .uponReceiving("location of 'fake-station' updated")
                .headers(headers())
                .method("POST")
                .matchPath("/stationconfiguration/locations/.+",
                        "/stationconfiguration/locations/fake-station")
                .body(locationBody())
                .willRespondWith()
                .status(200)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "notExistingStation")
    void Should_confirm_update_even_for_not_existing_stations() {
        client.sendLocationUpdated("fake-station", givenLocation());
    }

    @Pact(provider = "old-platform", consumer = "station-configuration")
    RequestResponsePact existingStation(PactDslWithProvider builder) {
        return builder
                .given("station 'garo-katowice' exists")
                .uponReceiving("location of 'garo-katowice' updated")
                .headers(headers())
                .method("POST")
                .matchPath("/stationconfiguration/locations/.+",
                        "/stationconfiguration/locations/garo-katowice")
                .body(locationBody())
                .willRespondWith()
                .status(200)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "existingStation")
    void Should_confirm_update_for_existing_stations() {
        client.sendLocationUpdated("garo-katowice", givenLocation());
    }

    @NotNull
    private Map<String, String> headers() {
        return Map.of(
                "Authorization", "Basic " + base64("fake-user:fake-pass"),
                "Content-Type", "application/json"
        );
    }

    private DslPart locationBody() {
        return LambdaDsl.newJsonBody(r -> r.object("location", l -> l
                .stringType("city", "Amsterdam")
                .stringType("houseNumber", "3")
                .stringType("street", "Dusartstraat")
                .stringType("zipcode", "1072HS")
                .stringMatcher("country", "[A-Z]{3}", "NLD")
                .numberType("longitude", new BigDecimal("4.809561"))
                .numberType("latitude", new BigDecimal("52.352206"))
        )).build();
    }

    @NotNull
    private String base64(String credentials) {
        return new String(Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.ISO_8859_1)));
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
}
