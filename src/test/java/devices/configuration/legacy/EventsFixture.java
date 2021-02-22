package devices.configuration.legacy;

import devices.configuration.DomainEvent;
import devices.configuration.features.bootNotification.StationConnectedToCsc;
import devices.configuration.features.catalogue.StationsFixture;
import devices.configuration.features.catalogue.location.Location;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static devices.configuration.StationsFixture.randomStationName;

public class EventsFixture {

    public static StationConnectedToCsc stationConnectedToCsc(String stationName) {
        return new StationConnectedToCsc(stationName);
    }

    public static StationLocationUpdated stationLocationUpdated(String stationName) {
        return new StationLocationUpdated(stationName,
                fromLocation(StationsFixture.Locations.rooseveltlaanInGent()),
                true
        );
    }

    @NotNull
    public static DomainEvent fakeDomainEvent() {
        return new DomainEvent() {
        };
    }

    public static StationLocation fromLocation(Location location) {
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

    public static StationConnected stationConnected() {
        return new StationConnected(
                UUID.randomUUID(),
                Instant.now(),
                randomStationName(),
                "2.0.0",
                "OCPP-AZURE",
                "eMobility"
        );
    }
}
