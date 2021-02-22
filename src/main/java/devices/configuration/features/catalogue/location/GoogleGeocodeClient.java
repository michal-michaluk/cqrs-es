package devices.configuration.features.catalogue.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import devices.configuration.legacy.StationLocation;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleGeocodeClient {

    private final RestTemplate restTemplate;
    private final String geocodeUrl;
    private final String apiKey;

    @Autowired
    public GoogleGeocodeClient(
            RestTemplate restTemplate,
            @Value("${location.google.maps.geocode.url}") String geocodeUrl,
            @Value("${location.google.maps.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.geocodeUrl = geocodeUrl;
        this.apiKey = apiKey;
    }

    public Try<StationLocation> locationForCoordinates(GeoLocation coordinates) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("latlng", MessageFormat.format("{0},{1}",
                coordinates.getLatitude(), coordinates.getLongitude()));
        params.add("location_type", "ROOFTOP");
        params.add("result_type", "street_address");
        return requestGeoLocation(params).mapTry(LocationResponse::toLocation);
    }

    public Try<StationLocation> locationForAddress(String addressToQuery) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("address", addressToQuery);
        return requestGeoLocation(params).mapTry(LocationResponse::toLocation);
    }

    private Try<LocationResponse> requestGeoLocation(MultiValueMap<String, String> params) {
        params.add("key", apiKey);
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(geocodeUrl).queryParams(params).build();
        try {
            LocationResponse response = restTemplate.getForObject(uriComponents.toUri(), LocationResponse.class);
            if (Objects.isNull(response) || response.isNOk()) {
                return Try.failure(new RuntimeException("Geocoding failed for request " + uriComponents.toUriString() + " with no result from google service " + response));
            }
            return Try.success(response);
        } catch (Throwable e) {
            return Try.failure(new RuntimeException("Geocoding failed for request " + uriComponents.toUriString() + " with cause " + e.getMessage(), e));
        }
    }

    static class Fields {
        Map<String, Function<AddressComponents, String>> definition = Map.of(
                "country", AddressComponents::getShortName,
                "locality", AddressComponents::getLongName,
                "postal_town", AddressComponents::getLongName,
                "street_number", AddressComponents::getLongName,
                "route", AddressComponents::getLongName,
                "postal_code", AddressComponents::getLongName
        );
        Set<String> optional = Set.of("postal_town");

        Map.Entry<Optional<String>, AddressComponents> mapToInterestingEntry(AddressComponents ac) {
            return Map.entry(
                    // "types" : [ "locality", "political" ] gives Optional["locality"]
                    // "types" : [ "not_interesting_field" ] gives Optional.empty
                    ac.getTypes().stream().filter(definition::containsKey).findFirst(),
                    ac
            );
        }

        String getValue(Map.Entry<Optional<String>, AddressComponents> e) {
            if (e.getKey().isPresent()) {
                return definition.get(e.getKey().get()).apply(e.getValue());
            }
            return null;
        }

        String fieldsMissingIn(Map<String, String> fields) {
            return this.definition.keySet().stream()
                    .filter(f -> !optional.contains(f))
                    .filter(f -> !fields.containsKey(f)
                            || StringUtils.isBlank(fields.get(f)))
                    .collect(Collectors.joining(", "));
        }

        void setMissingFieldsFromAlternatives(Map<String, String> fields) {
            if (fields.containsKey("postal_town") && StringUtils.isBlank(fields.get("locality"))) {
                fields.put("locality", fields.get("postal_town"));
            }
        }

        StationLocation createLocation(Map<String, String> fields, Coordinates coordinates) {
            return new StationLocation(
                    fields.get("locality"),
                    fields.get("street_number"),
                    fields.get("route"),
                    fields.get("postal_code"),
                    new Locale("", fields.get("country")).getISO3Country(),
                    coordinates.getLng(),
                    coordinates.getLat()
            );
        }
    }

    @lombok.Value
    static class LocationResponse {

        static Fields interestingFields = new Fields();
        @JsonProperty("error_message")
        String errorMessage;
        List<Results> results;
        String status;

        boolean isNOk() {
            return !"ok".equalsIgnoreCase(status);
        }

        public StationLocation toLocation() {
            Map<String, String> fields = results.stream()
                    .flatMap(result -> result.getAddressComponents().stream())
                    .map(ac -> interestingFields.mapToInterestingEntry(ac))
                    .filter(e -> e.getKey().isPresent())
                    .collect(Collectors.toMap(
                            e -> e.getKey().get(),
                            e -> interestingFields.getValue(e),
                            (v1, v2) -> v1
                    ));
            interestingFields.setMissingFieldsFromAlternatives(fields);
            String missingFields = interestingFields.fieldsMissingIn(fields);
            if (!missingFields.isBlank()) {
                throw new IllegalStateException("Geocoding response has fields missing: " + missingFields + "; found fields: " + fields + "; in geocode response: " + this);
            }
            Coordinates coordinates = results.stream()
                    .map(r -> r.getGeometry().getLocation())
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("missing results.geometry.location"));

            return interestingFields.createLocation(fields, coordinates);
        }
    }

    @lombok.Value
    static class Results {
        @JsonProperty("address_components")
        List<AddressComponents> addressComponents;
        @JsonProperty("formatted_address")
        String formattedAddress;
        Geometry geometry;
        @JsonProperty("place_id")
        String placeId;
        List<String> types;
    }

    @lombok.Value
    static class AddressComponents {
        @JsonProperty("long_name")
        String longName;
        @JsonProperty("short_name")
        String shortName;
        List<String> types;
    }

    @lombok.Value
    static class Geometry {
        Bounds bounds;
        Coordinates location;
        @JsonProperty("location_type")
        String locationType;
        Viewport viewport;
    }

    @lombok.Value
    static class Bounds {
        Coordinates northeast;
        Coordinates southwest;
    }

    @lombok.Value
    static class Viewport {
        Coordinates northeast;
        Coordinates southwest;
    }

    @lombok.Value
    static class Coordinates {
        BigDecimal lat;
        BigDecimal lng;
    }
}