package devices.configuration.features.catalogue.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Slf4j
@Service
public class GoogleAutocompleteClient {

    private final RestTemplate restTemplate;
    private final String autocompleteUrl;
    private final String apiKey;

    @Autowired
    public GoogleAutocompleteClient(
            RestTemplate restTemplate,
            @Value("${location.google.maps.autocomplete.url}") String autocompleteUrl,
            @Value("${location.google.maps.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.autocompleteUrl = autocompleteUrl;
        this.apiKey = apiKey;
    }

    public String locationAutoCompleted(String input) {
        try {
            return restTemplate.getForObject(uriComponents(input).toUri(), String.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getRawStatusCode()), e.getMessage());
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    private UriComponents uriComponents(String input) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("input", input);
        params.add("key", apiKey);
        return fromHttpUrl(autocompleteUrl).queryParams(params).build();
    }
}