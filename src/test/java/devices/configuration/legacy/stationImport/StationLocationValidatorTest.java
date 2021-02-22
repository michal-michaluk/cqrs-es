package devices.configuration.legacy.stationImport;

import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.legacy.StationLocationValidator;
import org.junit.jupiter.api.Test;

import static devices.configuration.legacy.StationLocationValidator.DEFAULT_POSTAL_CODE;
import static org.assertj.core.api.Assertions.assertThat;

class StationLocationValidatorTest {

    @Test
    void Should_return_empty_for_fully_filled_location() {
        // given
        var location = locationWithAddress().setCoordinates(new GeoLocation()
                .setLatitude("22.45096")
                .setLongitude("23.45094"));

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void Should_return_violation_for_blank_location_with_null_coordinates() {
        // given
        var location = blankLocation().setCoordinates(null);

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_return_violation_for_blank_location_with_only_lon() {
        // given
        var location = blankLocation()
                .setCoordinates(new GeoLocation()
                        .setLatitude("")
                        .setLongitude("23.45094"));

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_return_violation_for_blank_location_with_filled_coordinates() {
        // given
        var location = blankLocation()
                .setCoordinates(new GeoLocation()
                        .setLatitude("22.45096")
                        .setLongitude("23.45094"));

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_return_violation_for_filled_location_with_blank_coordinates() {
        // given
        var location = locationWithAddress().setCoordinates(blankCoordinates());

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_find_issue_in_location_with_0000AA_postal_code() {
        // given
        var location = locationWithAddress().setPostalCode(DEFAULT_POSTAL_CODE);

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_find_issue_in_location_with_UNKNOWN_postal_code() {
        // given
        var location = locationWithAddress().setPostalCode("UNKNOWN");

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_find_issue_in_location_with_UNKNOWN_city() {
        // given
        var location = locationWithAddress().setCity("UNKNOWN");

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void Should_find_issue_in_location_with_UNKNOWN_street() {
        // given
        var location = locationWithAddress().setStreet("UNKNOWN");

        // when
        var result = whenValidating(location);

        // then
        assertThat(result).isNotEmpty();
    }

    private String whenValidating(Location location) {
        return new StationLocationValidator()
                .issues(null, location);
    }

    private GeoLocation blankCoordinates() {
        return new GeoLocation()
                .setLatitude("")
                .setLongitude("");
    }

    private Location blankLocation() {
        return new Location()
                .setState("")
                .setStreet("")
                .setPostalCode("")
                .setHouseNumber("")
                .setCity("")
                .setCountryISO("DE");
    }

    private Location locationWithAddress() {
        return new Location()
                .setState("Śląsk")
                .setStreet("Francuska")
                .setPostalCode("44-100")
                .setHouseNumber("243")
                .setCity("Katowice")
                .setCountryISO("DE");
    }
}