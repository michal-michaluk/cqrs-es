package devices.configuration.published;

import devices.configuration.DomainEvent;
import devices.configuration.features.catalogue.Station;
import lombok.Value;

import java.util.List;

@Value
public class StationSnapshot {

    public static List<DomainEvent> updated(Station station) {
        return List.of(new StationSnapshotV1(StationReadModelV1.from(station), false));
    }

    public static List<DomainEvent> deleted(Station station) {
        return List.of(new StationSnapshotV1(StationReadModelV1.from(station), true));
    }
}
