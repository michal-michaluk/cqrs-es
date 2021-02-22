package devices.configuration.legacy;

import devices.configuration.features.catalogue.StationUpdate;
import lombok.Value;

/**
 * Incoming event from eggplant
 **/
@Value
public class StationMetadataUpdated {

    String stationName;
    StationLocation location;
    // some of fields not mapped for our purpose

    public StationUpdate toStationUpdate() {
        return new StationUpdate().setLocation(location.toLocation()
                .setUpdate(false));
    }
}
