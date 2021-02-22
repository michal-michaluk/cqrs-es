package devices.configuration.legacy;

import devices.configuration.features.Toggles;
import devices.configuration.features.catalogue.location.GoogleGeocodeClient;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.toggle.TogglesService;
import devices.configuration.features.catalogue.location.GeoLocation;
import io.vavr.control.Try;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.vavr.API.*;
import static java.util.function.Predicate.not;

@Service
public class LocationFixing {

    private static final GeoLocation.Point bottomLeft = GeoLocation.Point.of("32.570262", "-28.716926");
    private static final GeoLocation.Point topRight = GeoLocation.Point.of("76.419987", "85.820474");

    private GoogleGeocodeClient geocoder;
    private TogglesService toggles;

    public LocationFixing(GoogleGeocodeClient geocoder, TogglesService toggles) {
        this.geocoder = geocoder;
        this.toggles = toggles;
    }

    final Predicate<String> missing = value -> Objects.isNull(value) || value.isBlank();
    final Predicate<String> unknown = value -> value.toUpperCase().equals("UNKNOWN");
    final Predicate<String> badNumber = value -> Try.of(() -> new BigDecimal(value)).isFailure();
    final Predicate<Location> fakePostalCode = location -> location.getPostalCode().toUpperCase().equals("0000AA");
    final Predicate<Location> coordinatesInRect = location ->
            location.getCoordinates().toPoint().inRect(bottomLeft, topRight);
    final Predicate<Location> hasAddress = location ->
            Stream.of(location.getCountryISO(), location.getCity(), location.getPostalCode(), location.getStreet(), location.getHouseNumber())
                    .noneMatch(missing.or(unknown));
    final Predicate<Location> hasCoordinates = location -> Objects.nonNull(location.getCoordinates()) &&
            Stream.of(location.getCoordinates().getLatitude(), location.getCoordinates().getLongitude())
                    .noneMatch(missing.or(unknown).or(badNumber));

    final Predicate<Location> isEnabled = l -> toggles.isEnabled(Toggles.FIX_LOCATIONS_FROM_EGGPLANT, false);
    final Predicate<Location> hasCorrectAddress = hasAddress.and(not(fakePostalCode));
    final Predicate<Location> hasCorrectCoordinates = hasCoordinates.and(coordinatesInRect);
    final Predicate<Location> allFine = hasCorrectAddress.and(hasCorrectCoordinates);
    final Predicate<Location> canFixCoordinates = hasCorrectAddress.and(not(hasCorrectCoordinates));
    final Predicate<Location> canFixAddress = not(hasCorrectAddress).and(hasCorrectCoordinates);

    final Function<Location, Try<StationLocation>> geocode = location ->
            geocoder.locationForAddress(
                    String.format("%s %s, %s %s",
                            location.getCity(), location.getPostalCode(),
                            location.getStreet(), location.getHouseNumber())
            );

    final Function<Location, Try<StationLocation>> reverseGeocode = location ->
            geocoder.locationForCoordinates(location.getCoordinates());

    final Function<Location, Status> applyFixes = location -> Match(location).of(
            Case($(allFine), Status::notTouched),
            Case($(isEnabled.and(canFixAddress)), () -> reverseGeocode.apply(location)
                    .onSuccess(fix -> fix.overrideAddressIn(location))
                    .map(Status::fixedAddress).recover(Status::failure).get()),
            Case($(isEnabled.and(canFixCoordinates)), () -> geocode.apply(location)
                    .onSuccess(fix -> fix.overrideCoordinatesIn(location))
                    .map(Status::fixedCoordinates).recover(Status::failure).get()),
            Case($(), Status.noFixPossible(location))
    );

    public Status process(Location location) {
        return applyFixes.apply(location);
    }

    public interface Status {

        static Status notTouched() {
            return new NotTouched();
        }

        static Status fixedAddress(StationLocation fix) {
            return new Fixed(fix);
        }

        static Status fixedCoordinates(StationLocation fix) {
            return new Fixed(fix);
        }

        static Status failure(Throwable throwable) {
            return new Failure(throwable);
        }

        static Status noFixPossible(Location location) {
            return new Failure(new Throwable(location + " not eligible for geocoding or reverse geocoding"));
        }

        default Status onFixed(Consumer<StationLocation> fun) {
            if (this instanceof Fixed) fun.accept(((Fixed) this).fix);
            return this;
        }

        default Status onFailure(Consumer<Throwable> fun) {
            if (this instanceof Failure) fun.accept(((Failure) this).throwable);
            return this;
        }

        default boolean isFailure() {
            return this instanceof Failure;
        }

        @Value
        class NotTouched implements Status {
        }

        @Value
        class Fixed implements Status {
            StationLocation fix;
        }

        @Value
        class Failure implements Status {
            Throwable throwable;
        }
    }
}
