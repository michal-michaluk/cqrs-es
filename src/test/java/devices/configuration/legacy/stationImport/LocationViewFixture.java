package devices.configuration.legacy.stationImport;

import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import org.apache.commons.lang3.RandomStringUtils;

public class LocationViewFixture {

    static Location location(String stationName) {
        return new Location()
                .setStreet(stationName + " Street")
                .setHouseNumber("1")
                .setCity("Kato")
                .setPostalCode("24-242")
                .setCountryISO("POL")
                .setState("Śląsk")
                .setCoordinates(new GeoLocation()
                        .setLatitude("12.2424")
                        .setLongitude("22.5644"));
    }

    static StationView.LocationView locationView(String stationName) {
        return StationView.LocationView.map(location(stationName));
    }

    static Location location() {
        return new Location()
                .setStreet(RandomStringUtils.randomAlphanumeric(10))
                .setHouseNumber("1")
                .setCity("Kato")
                .setPostalCode("24-242")
                .setCountryISO("POL")
                .setState("Śląsk")
                .setCoordinates(new GeoLocation()
                        .setLatitude("12.2424")
                        .setLongitude("22.5644"));
    }
}
