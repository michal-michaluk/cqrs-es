package devices.configuration.features.catalogue.location;

import devices.configuration.legacy.StationLocation;
import io.vavr.control.Try;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;

import java.math.BigDecimal;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GoogleGeocodeClient.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@TestPropertySource(properties = {
        "location.google.maps.geocode.url=https://maps.googleapis.com/maps/api/geocode/json-TEST",
        "location.google.maps.api.key=FAKE_API_KEY"
})
class GoogleGeocodeClientTest {

    @Value("${location.google.maps.geocode.url}")
    String url;

    @Autowired
    GoogleGeocodeClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void Should_convert_coordinates_to_complete_station_location() {
        // given
        expectRequest("latlng=52.352206,4.809561&location_type=ROOFTOP&result_type=street_address")
                .andRespond(withSuccess(GoogleResponsesFixture.addressForCoordinates(), MediaType.APPLICATION_JSON));

        // when
        var res = client.locationForCoordinates(new GeoLocation()
                .setLatitude("52.352206")
                .setLongitude("4.809561")
        );

        // then
        Assertions.assertThat(res).isEqualTo(Try.success(
                new StationLocation(
                        "Amsterdam",
                        "34", "Fien de la Mar Straat",
                        "1068 SG", "NLD",
                        new BigDecimal("4.8094816"),
                        new BigDecimal("52.35222160000001")
                )
        ));
        server.verify();
    }

    @Test
    void Should_handle_empty_results() {
        // given
        expectRequest("latlng=52.352206,4.809561&location_type=ROOFTOP&result_type=street_address")
                .andRespond(withSuccess(GoogleResponsesFixture.zeroResults(), MediaType.APPLICATION_JSON));

        // expected
        Assertions.assertThat(
                client.locationForCoordinates(new GeoLocation()
                        .setLatitude("52.352206")
                        .setLongitude("4.809561")
                ).getCause()
        ).hasMessageContaining("errorMessage=null, results=[], status=ZERO_RESULTS");

        // and
        server.verify();
    }

    @Test
    void Should_convert_address_to_complete_station_location() {
        // given
        expectRequest("address=Wrocław%20Żwirki%20i%20Wigury%207")
                .andRespond(withSuccess(GoogleResponsesFixture.streetAddress(), MediaType.APPLICATION_JSON));

        // when
        var res = client.locationForAddress(
                "Wrocław Żwirki i Wigury 7"
        );

        // then
        Assertions.assertThat(res).isEqualTo(Try.success(
                new StationLocation(
                        "Wrocław",
                        "7", "Żwirki i Wigury",
                        "54-620", "POL",
                        new BigDecimal("16.934523"),
                        new BigDecimal("51.097765")
                )
        ));
        server.verify();
    }

    @Test
    void Should_convert_country_to_incomplete_station_location() {
        // given
        expectRequest("address=Wrocław")
                .andRespond(withSuccess(GoogleResponsesFixture.cityLevel(), MediaType.APPLICATION_JSON));

        // when
        var res = client.locationForAddress(
                "Wrocław"
        );

        // then
        Assertions.assertThat(res.getCause())
                .hasMessageFindingMatch("fields missing:.*route.*in geocode response")
                .hasMessageFindingMatch("fields missing:.*street_number.*in geocode response");
        server.verify();
    }

    private ResponseActions expectRequest(String query) {
        return server.expect(requestTo(url + "?" + query + "&key=FAKE_API_KEY"));
    }
}
