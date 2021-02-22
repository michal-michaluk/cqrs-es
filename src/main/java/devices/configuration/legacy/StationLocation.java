package devices.configuration.legacy;

import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class StationLocation {
    String city;
    String houseNumber;
    String street;
    String zipcode;
    String country;
    BigDecimal longitude;
    BigDecimal latitude;

    public static StationLocation from(Location location) {
        if (location == null) return null;
        return new StationLocation(
                location.getCity(),
                location.getHouseNumber(),
                location.getStreet(),
                location.getPostalCode(),
                location.getCountryISO(),
                new BigDecimal(location.getCoordinates().getLongitude()),
                new BigDecimal(location.getCoordinates().getLatitude())
        );
    }

    Location toLocation() {
        return new Location()
                .setStreet(street)
                .setHouseNumber(houseNumber)
                .setCity(city)
                .setPostalCode(zipcode)
                .setCountryISO(country)
                .setCoordinates(toGeoLocation());
    }

    GeoLocation toGeoLocation() {
        if (latitude == null || longitude == null) {
            return null;
        }
        return new GeoLocation()
                .setLatitude(latitude.toPlainString())
                .setLongitude(longitude.toPlainString());
    }

    void overrideAddressIn(Location location) {
        location.setCountryISO(country)
                .setCity(city)
                .setPostalCode(zipcode)
                .setStreet(street)
                .setHouseNumber(houseNumber);
    }

    void overrideCoordinatesIn(Location location) {
        location.setCoordinates(toGeoLocation());
    }
}
