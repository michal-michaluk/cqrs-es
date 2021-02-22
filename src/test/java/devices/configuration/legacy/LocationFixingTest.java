package devices.configuration.legacy;

import devices.configuration.features.Toggles;
import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.GoogleGeocodeClient;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.toggle.TogglesService;
import devices.configuration.features.catalogue.StationsFixture;
import io.vavr.control.Try;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static devices.configuration.legacy.StationLocation.from;
import static devices.configuration.legacy.StationLocationValidator.DEFAULT_POSTAL_CODE;
import static org.assertj.core.api.Assertions.assertThat;

class LocationFixingTest {
    GoogleGeocodeClient geocode = Mockito.mock(GoogleGeocodeClient.class);
    TogglesService toggles = Mockito.mock(TogglesService.class);

    LocationFixing fixing = new LocationFixing(geocode, toggles);

    @BeforeEach
    void setUp() {
        Mockito.when(toggles.isEnabled(Mockito.eq(Toggles.FIX_LOCATIONS_FROM_EGGPLANT), Mockito.anyBoolean())).thenReturn(true);
        Mockito.when(toggles.isDisabled(Mockito.eq(Toggles.FIX_LOCATIONS_FROM_EGGPLANT), Mockito.anyBoolean())).thenReturn(false);
    }

    @Test
    void Should_find_all_is_fine_for_correct_location() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent();

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isTrue();
        assertThat(fixing.coordinatesInRect.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isTrue();
    }

    @Test
    void Should_find_missing_coordinates() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(null);

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_coordinates_out_of_rect() {
        // given
        Location location = StationsFixture.Locations.chujtenInChina();

        // expected
        assertThat(fixing.coordinatesInRect.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_latitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLatitude(null));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_longitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLongitude(null));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_latitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLatitude(" "));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_longitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLongitude(" "));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_latitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLatitude("UNKNOWN"));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_longitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLongitude("UNKNOWN"));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_bad_number_in_latitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLatitude("not-a-number"));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_bad_number_in_longitude() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates().setLongitude("not-a-number"));

        // expected
        assertThat(fixing.hasCoordinates.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_CountryISO() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCountryISO(null);

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_City() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCity(null);

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_PostalCode() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setPostalCode(null);

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_Street() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setStreet(null);

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_missing_HouseNumber() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setHouseNumber(null);

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_CountryISO() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCountryISO(" ");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_City() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCity(" ");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_PostalCode() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setPostalCode(" ");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_Street() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setStreet(" ");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_blank_HouseNumber() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setHouseNumber(" ");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_CountryISO() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCountryISO("UNKNOWN");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_City() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setCity("UNKNOWN");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_PostalCode() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setPostalCode("UNKNOWN");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_Street() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setStreet("UNKNOWN");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_unknown_HouseNumber() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setHouseNumber("UNKNOWN");

        // expected
        assertThat(fixing.hasAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_fake_PostalCode() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setPostalCode(DEFAULT_POSTAL_CODE);

        // expected
        assertThat(fixing.fakePostalCode.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_find_fake_PostalCode_lower_case() {
        // given
        Location location = StationsFixture.Locations.rooseveltlaanInGent()
                .setPostalCode(DEFAULT_POSTAL_CODE.toLowerCase());

        // expected
        assertThat(fixing.fakePostalCode.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isTrue();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }

    @Test
    void Should_geocode() {
        // given
        Mockito.when(geocode.locationForAddress(Mockito.any()))
                .thenReturn(Try.success(from(StationsFixture.Locations.rooseveltlaanInGent())));

        // when
        Try<StationLocation> actual = fixing.geocode.apply(StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(null)
        );

        // then
        Mockito.verify(geocode).locationForAddress("Gent 9000, F.Rooseveltlaan 3A");
        Assertions.assertThat(actual)
                .isEqualTo(Try.success(from(StationsFixture.Locations.rooseveltlaanInGent())));
    }

    @Test
    void Should_reverse_geocode() {
        // given
        Mockito.when(geocode.locationForCoordinates(Mockito.any()))
                .thenReturn(Try.success(from(StationsFixture.Locations.rooseveltlaanInGent())));

        // when
        Try<StationLocation> actual = fixing.reverseGeocode.apply(StationsFixture.Locations.rooseveltlaanInGent()
                .setCity(null)
                .setStreet(null)
        );

        // then
        Mockito.verify(geocode).locationForCoordinates(StationsFixture.Locations.rooseveltlaanInGent().getCoordinates());
        Assertions.assertThat(actual)
                .isEqualTo(Try.success(from(StationsFixture.Locations.rooseveltlaanInGent())));
    }

    @Test
    void Should_choose_to_do_nothing_for_correct_location() {
        // given
        Location givenLocation = StationsFixture.Locations.rooseveltlaanInGent();

        // when
        LocationFixing.Status actual = fixing.applyFixes.apply(givenLocation);

        // then
        assertThat(actual).isEqualTo(LocationFixing.Status.notTouched());
        Mockito.verifyNoInteractions(geocode);
    }

    @Test
    void Should_choose_to_fix_address() {
        // given
        Location givenLocation = StationsFixture.Locations.rooseveltlaanInGent()
                .setStreet("UNKNOWN");
        Mockito.when(geocode.locationForCoordinates(Mockito.any()))
                .thenReturn(Try.success(from(StationsFixture.Locations.rooseveltlaanInGent())));

        // when
        LocationFixing.Status actual = fixing.applyFixes.apply(givenLocation);

        // then
        assertThat(actual).isEqualTo(LocationFixing.Status.fixedAddress(from(StationsFixture.Locations.rooseveltlaanInGent())));
    }

    @Test
    void Should_choose_to_fix_coordinates() {
        // given
        Location givenLocation = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(null);
        Mockito.when(geocode.locationForAddress(Mockito.any()))
                .thenReturn(Try.success(from(StationsFixture.Locations.rooseveltlaanInGent())));

        // when
        LocationFixing.Status actual = fixing.applyFixes.apply(givenLocation);

        // then
        assertThat(actual).isEqualTo(LocationFixing.Status.fixedCoordinates(from(StationsFixture.Locations.rooseveltlaanInGent())));
    }

    @Test
    void Should_choose_to_do_nothing_for_broken_location() {
        // given
        Location givenLocation = StationsFixture.Locations.rooseveltlaanInGent()
                .setStreet("UNKNOWN")
                .setCoordinates(null);

        // when
        LocationFixing.Status actual = fixing.applyFixes.apply(givenLocation);

        // then
        Mockito.verifyNoInteractions(geocode);
    }

    @Test
    void Should_handle_exception_while_geocode() {
        // given
        Location givenLocation = StationsFixture.Locations.rooseveltlaanInGent()
                .setCoordinates(null);
        Mockito.when(geocode.locationForAddress(Mockito.any()))
                .thenReturn(Try.failure(new RuntimeException("Arbitrary Fail")));

        // when
        LocationFixing.Status actual = fixing.applyFixes.apply(givenLocation);

        // then
        assertThat(actual.isFailure()).isTrue();
    }

    @Test
    void Should_handle_exception_while_reverse_geocode() {
        // given
        Location givenLocation = StationsFixture.Locations.rooseveltlaanInGent()
                .setCity("UNKNOWN");
        Mockito.when(geocode.locationForCoordinates(Mockito.any()))
                .thenReturn(Try.failure(new RuntimeException("Arbitrary Fail")));

        // when
        LocationFixing.Status actual = fixing.applyFixes.apply(givenLocation);

        // then
        assertThat(actual.isFailure()).isTrue();
    }

    @Test
    void stockholmCase() {
        var location = new Location()
                .setCity("Stockholm")
                .setStreet("Nybohovsbacken")
                .setHouseNumber("2")
                .setPostalCode("11763")
                .setState("null")
                .setCountryISO("SWE")
                .setCoordinates(new GeoLocation()
                        .setLatitude("18.02522000000000000000")
                        .setLongitude("18.02722000000000000000")
                );

        // expected
        assertThat(fixing.fakePostalCode.test(location))
                .isFalse();
        assertThat(fixing.hasCorrectAddress.test(location))
                .isTrue();
        assertThat(fixing.hasCorrectCoordinates.test(location))
                .isFalse();
        assertThat(fixing.allFine.test(location))
                .isFalse();
    }
}
