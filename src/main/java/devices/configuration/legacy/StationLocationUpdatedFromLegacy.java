package devices.configuration.legacy;

import devices.configuration.features.catalogue.StationUpdate;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Incoming event for eggplant.
 **/
@Value
class StationLocationUpdatedFromLegacy {
    UUID id;
    Instant occurrenceTime;
    List<String> stations;
    StationLocation newLocation;

    public StationUpdate toStationUpdate() {
        return new StationUpdate().setLocation(
                newLocation.toLocation()
                        .setUpdate(false));
    }
}
