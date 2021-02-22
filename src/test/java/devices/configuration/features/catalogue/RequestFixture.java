package devices.configuration.features.catalogue;

import devices.configuration.SecurityFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Lazy
public class RequestFixture {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    SecurityFixture securityFixture;


    public ResponseEntity<String> getStations() {
        return testRestTemplate.exchange(
                "/installation/stations/", HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()), String.class);
    }

    public ResponseEntity<String> getStationsWithNameLike(String stationName) {
        return testRestTemplate.exchange(
                "/installation/stations/?name={stationName}", HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()), String.class,
                stationName);
    }

    public ResponseEntity<String> getStation(String stationName) {
        return testRestTemplate.exchange(
                "/installation/stations/{stationName}", HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()), String.class,
                stationName);
    }

    public ResponseEntity<String> updateStation(String stationName, StationUpdate update) {
        return testRestTemplate.exchange(
                "/installation/stations/{stationName}", HttpMethod.PATCH,
                new HttpEntity<>(update, jsonHeaders()), String.class,
                stationName
        );
    }

    public ResponseEntity<String> importStations(FileSystemResource file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("stations", file);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, multipartFormDataHeaders());

        return testRestTemplate.postForEntity("/installation/stations", requestEntity, String.class);
    }

    public ResponseEntity<String> importStations(FileSystemResource stations, FileSystemResource photo) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("stations", stations);
        body.add("stationsPhoto", photo);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, multipartFormDataHeaders());

        return testRestTemplate.postForEntity("/installation/stations", requestEntity, String.class);
    }

    public ResponseEntity<String> exportStations() {
        var headers = headersWithJwtToken();
        headers.add("Accept", "text/csv");
        return testRestTemplate.exchange(
                "/installation/stations/", HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = headersWithJwtToken();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    private HttpHeaders multipartFormDataHeaders() {
        HttpHeaders headers = headersWithJwtToken();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private HttpHeaders headersWithJwtToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, securityFixture.operatorToken());
        return headers;
    }
}
