package devices.configuration.legacy.stationImport;

import devices.configuration.features.catalogue.location.Location;

import java.time.Instant;
import java.util.Date;

import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class StationViewFixture {

    public static final String ATTACHED_CABLE_TYPE_1_MODE_3 = "ATTACHED_CABLE_TYPE_1_MODE_3";

    static StationView.ChargingStation stationViewWithLocation(String stationName) {
        return station(stationName, ATTACHED_CABLE_TYPE_1_MODE_3, LocationViewFixture.locationView(stationName), party(), lcp());
    }

    static StationView.ChargingStation stationViewWithLocation(String stationName, Location location) {
        return station(stationName, ATTACHED_CABLE_TYPE_1_MODE_3, StationView.LocationView.map(location), party(), lcp());
    }

    static StationView.ChargingStation stationViewWithPlugType(String stationName, String plugType) {
        return station(stationName, plugType, LocationViewFixture.locationView(stationName), party(), lcp());
    }

    private static StationView.ChargingStation station(String stationName, String plugTypeName, StationView.LocationView location, StationView.Party cpo, StationView.Lcp lcp) {
        return new StationView.ChargingStation(1L, stationName, stationType(stationName), singletonList(point(stationName, plugTypeName)), location, null,
                false, someDate(), "any", null, null, false, false, null, "any", null, "any",
                "any", "any", true, true, true, true, true, true, "any", "any", "any",
                "any", cpo, lcp, 1L, null, null, null, false, null, emptyList());
    }

    private static StationView.ChargingPoint point(String stationName, String plugTypeName) {
        return new StationView.ChargingPoint(1L, stationName + "_evse", pointType(stationName, plugTypeName), 1L, 1,
                stationName + "_1", ONE, "any", someDate(), true, "any", false,
                null, someDate(), "any", null, emptyList(), null, null, "any", null, "PUBLIC");
    }

    private static StationView.ChargingPointType pointType(String stationName, String plugTypeName) {
        return new StationView.ChargingPointType(1L, stationName + "_pointType", singletonList(plugType(plugTypeName)), ratingsType(), stationName + "_type", false);
    }

    private static StationView.ChargingStationType stationType(String stationName) {
        return new StationView.ChargingStationType(1L, stationName + "_stationType", "GARO", false, false, false, "http://localhost",
                true, false, null, null, "any", null);
    }

    private static StationView.RatingsType ratingsType() {
        return new StationView.RatingsType(1L, ONE, ONE, ONE, ONE);
    }

    private static StationView.PlugType plugType(String plugTypeName) {
        return new StationView.PlugType(1L, plugTypeName, "any", 1L, "any");
    }

    private static StationView.Party party() {
        return new StationView.Party(1L, "any", true, true, true, "any");
    }

    private static StationView.Lcp lcp() {
        return new StationView.Lcp(1L, "any", "any", 1L, "any");
    }

    private static Date someDate() {
        return Date.from(Instant.parse("2020-01-03T10:15:30Z"));
    }
}
