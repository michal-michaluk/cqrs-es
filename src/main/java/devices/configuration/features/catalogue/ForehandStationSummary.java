package devices.configuration.features.catalogue;

import devices.configuration.features.catalogue.location.Location;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@Builder
public class ForehandStationSummary {
    String name;
    String description;
    List<String> address;
    List<String> functions;
    Stats chargingPointsStats;

    public static ForehandStationSummary from(Station station) {
        return ForehandStationSummary.builder()
                .name(station.getName())
                .description(station.getPhysicalReference())
                .address(Optional.ofNullable(station.getLocation())
                        .map(Location::toAddressDescription)
                        .orElse(List.of()))
                .functions(List.of())
                .chargingPointsStats(new Stats())
                .build();
    }

    @Value
    public static class Stats {
        long connection_error = 1;
        long unavailable = 1;
        long reserved = 1;
        long available = 1;
        long charging = 1;
        long error = 1;
        long unknown = 1;
    }
}
