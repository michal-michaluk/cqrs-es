package devices.configuration.published;

import devices.configuration.DomainEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class StationSnapshotV1 implements DomainEvent {

    StationReadModelV1 station;
    boolean deleted;

    public String getStationName() {
        return station.getName();
    }
}
