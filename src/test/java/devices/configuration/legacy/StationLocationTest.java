package devices.configuration.legacy;

import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.StationsFixture;
import org.junit.jupiter.api.Test;

import static devices.configuration.legacy.StationLocation.from;
import static org.assertj.core.api.Assertions.assertThat;

class StationLocationTest {

    @Test
    void Should_fix_address() {
        // given
        StationLocation fix = from(StationsFixture.Locations.rooseveltlaanInGent());
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCity(null)
                .setStreet(null);

        // when
        fix.overrideAddressIn(location);

        // then
        assertThat(location)
                .extracting(Location::getCity, Location::getStreet)
                .containsExactly("Gent", "F.Rooseveltlaan");
    }

    @Test
    void Should_fix_coordinates() {
        // given
        StationLocation fix = from(StationsFixture.Locations.rooseveltlaanInGent());
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(null);

        // when
        fix.overrideCoordinatesIn(location);

        // then
        assertThat(location.getCoordinates())
                .extracting(GeoLocation::getLatitude, GeoLocation::getLongitude)
                .containsExactly("51.047599", "3.729944");
    }
}
