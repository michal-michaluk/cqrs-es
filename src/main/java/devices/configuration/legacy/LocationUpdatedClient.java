package devices.configuration.legacy;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
class LocationUpdatedClient {

    @Value("${emobility.url}")
    private String url;
    @Value("${emobility.username}")
    private String username;
    @Value("${emobility.password}")
    private String password;
    @Autowired
    private RestTemplate restTemplate;

    @EventListener
    public void handle(StationLocationUpdated event) {
        if (event.isUpdate()) {
            sendLocationUpdated(event.getStationName(), LocationUpdatedFields.from(event));
        }
    }

    void sendLocationUpdated(String stationName, LocationUpdatedFields locationUpdatedFields) {
        try {
            Retry.ofDefaults("SendLocationUpdatedToEMobility").executeRunnable(() ->
                    restTemplate.exchange(url + "/stationconfiguration/locations/{stationName}",
                            HttpMethod.POST, payload(locationUpdatedFields), Void.class,
                            Map.of("stationName", stationName)
                    )
            );
        } catch (Exception e) {
            log.warn("Error while sending LocationUpdatedFields to eMobility old platform, station: {},  body: {}",
                    stationName, locationUpdatedFields, e
            );
        }
    }

    private <T> HttpEntity<T> payload(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return new HttpEntity<>(body, headers);
    }

    @lombok.Value
    static class LocationUpdatedFields {
        StationLocation location;

        static LocationUpdatedFields from(StationLocationUpdated updated) {
            return new LocationUpdatedFields(updated.getLocation());
        }
    }
}
