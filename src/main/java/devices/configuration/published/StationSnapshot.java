package devices.configuration.published;

import devices.configuration.DomainEvent;
import devices.configuration.data.Device;
import lombok.Value;

import java.util.List;

@Value
public class StationSnapshot {

    public static List<DomainEvent> updated(Device station) {
        return List.of(new StationSnapshotV1(StationReadModelV1.from(station), false));
    }

    public static List<DomainEvent> deleted(Device station) {
        return List.of(new StationSnapshotV1(StationReadModelV1.from(station), true));
    }
}
