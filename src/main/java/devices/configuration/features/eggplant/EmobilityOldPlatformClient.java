package devices.configuration.features.eggplant;

import devices.configuration.features.bootNotification.BootNotificationFields;
import devices.configuration.features.catalogue.fileImport.eggplant.StationsFileImportReport;
import devices.configuration.legacy.stationImport.RestPage;
import devices.configuration.legacy.stationImport.StationImportFailed;
import devices.configuration.legacy.stationImport.StationView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class EmobilityOldPlatformClient {

    private static final int LONGER_TASK_READ_TIMEOUT_IN_SECONDS = 100;
    private static final int LONGER_TASK_CONNECT_TIMEOUT_IN_SECONDS = 10;

    private final String url;
    private final String username;
    private final String password;
    private final RestTemplate restTemplate;
    private final RestTemplate longTimeoutRestTemplate;

    EmobilityOldPlatformClient(
            @Value("${emobility.url}") String url,
            @Value("${emobility.username}") String username,
            @Value("${emobility.password}") String password,
            @Autowired RestTemplate restTemplate,
            @Autowired RestTemplateBuilder restTemplateBuilder) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.restTemplate = restTemplate;

        longTimeoutRestTemplate = restTemplateBuilder
                .setReadTimeout(ofSeconds(LONGER_TASK_READ_TIMEOUT_IN_SECONDS))
                .setConnectTimeout(ofSeconds(LONGER_TASK_CONNECT_TIMEOUT_IN_SECONDS))
                .build();
    }

    @Retryable(
            value = {ResourceAccessException.class, HttpServerErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 250, multiplier = 2))
    public ResponseEntity<String> sendOcppMessage(String endpoint, String actionName, String stationName, Object body) {
        HttpHeaders headers = basicAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity(url + endpoint + "/" + actionName + "/{stationName}", new HttpEntity<>(body, headers), String.class, stationName);
    }

    public StationsFileImportReport importStationsFile(MultipartFile stations, ImportRequest importRequest, String username) {
        String fileContent = extractContent(stations);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url + "/stationconfiguration/stations")
                .queryParam("country", importRequest.getCountry().name())
                .queryParam("chargingStationTypeName", importRequest.getChargingStationTypeName())
                .queryParam("chargingPointTypeName", importRequest.getChargingPointTypeName())
                .queryParam("cpoName", importRequest.getCpoName())
                .queryParam("requestor", username)
                .build();

        try {
            ResponseEntity<StationsFileImportReport> response = longTimeoutRestTemplate.exchange(
                    uriComponents.toUri(),
                    POST,
                    new HttpEntity<>(fileContent, basicAuthHeaders()),
                    StationsFileImportReport.class);

            requireStatusCreated(response);

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unable to upload file to Eggplant", e);
        }

    }

    private String extractContent(MultipartFile stations) {
        String fileContent = null;
        try {
            fileContent = IOUtils.toString(stations.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public void sendStationConnected(String stationName, BootNotificationFields bootNotificationFields) {
        HttpEntity<BootNotificationFields> httpEntity = new HttpEntity<>(bootNotificationFields, basicAuthHeaders());
        restTemplate.exchange(URI.create(
                url + "/stationconfiguration/stationconnected/" + stationName),
                POST,
                httpEntity,
                Void.class);
    }

    @Retryable(
            value = {StationImportFailed.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10 * 1000))
    public RestPage<StationView.ChargingStation> getPageOfStations(int page, int size) {
        ResponseEntity<RestPage<StationView.ChargingStation>> response = getPage(page, size);
        requireOk(response);
        return response.getBody();
    }

    @Retryable(value = {ResourceAccessException.class, HttpServerErrorException.class})
    public String getStationExport(String stationName) {
        return restTemplate.exchange(url + "/stationconfiguration/stations/{stationName}",
                GET,
                new HttpEntity<>(basicAuthHeaders()),
                String.class,
                stationName
        ).getBody();
    }

    private void requireStatusCreated(ResponseEntity<?> response) {
        String responseMessage = "The response code from Eggplant was " + response.getStatusCode() + " with body " + response.getBody();
        log.info(responseMessage);

        if (!response.getStatusCode().equals(CREATED)) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Expected 201, while " + responseMessage);
        }
    }

    private void requireOk(ResponseEntity<RestPage<StationView.ChargingStation>> response) {
        if (!response.getStatusCode().equals(OK)) {
            throw new StationImportFailed(response.getStatusCodeValue());
        }
    }

    private ResponseEntity<RestPage<StationView.ChargingStation>> getPage(int page, int size) {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate.exchange(url + "/stationconfiguration/stations?page={page}&size={size}",
                GET,
                new HttpEntity<>(basicAuthHeaders()),
                new ParameterizedTypeReference<>() {
                },
                page, size);
    }

    private HttpHeaders basicAuthHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(username, password);
        return httpHeaders;
    }
}
