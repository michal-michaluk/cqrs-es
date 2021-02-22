package devices.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import devices.configuration.features.bootNotification.StationConnectedToCsc;
import devices.configuration.features.bootNotification.StationConnectedToOldPlatform;
import devices.configuration.features.bootNotification.StationProtocolChanged;
import devices.configuration.features.stationManagement.StationCreated;
import devices.configuration.features.stationManagement.StationDeleted;
import devices.configuration.legacy.StationLocationUpdated;
import devices.configuration.outbox.OutboxConfiguration;
import devices.configuration.published.StationSnapshotV1;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StationLocationUpdated.class, name = "StationLocationUpdated_v1"),
        @JsonSubTypes.Type(value = StationProtocolChanged.class, name = "StationProtocolChanged_v1"),
        @JsonSubTypes.Type(value = StationConnectedToOldPlatform.class, name = "StationConnectedToOldPlatform_v1"),
        @JsonSubTypes.Type(value = StationConnectedToCsc.class, name = "StationConnectedToCsc_v1"),
        @JsonSubTypes.Type(value = StationCreated.class, name = "StationCreated_v1"),
        @JsonSubTypes.Type(value = StationDeleted.class, name = "StationDeleted_v1"),
        @JsonSubTypes.Type(value = StationSnapshotV1.class, name = "StationSnapshot_v1"),
})
public class EventTypes {

    final static OutboxConfiguration outbox = OutboxConfiguration.builder()
            .publish(StationLocationUpdated.class, "station-configuration", MessagesKeyProvider.withType(StationLocationUpdated::getStationName))
            .publish(StationProtocolChanged.class, "station-configuration", MessagesKeyProvider.withType(StationProtocolChanged::getStationName))
            .publish(StationConnectedToOldPlatform.class, "station-configuration", MessagesKeyProvider.withTypeStationConnected(StationConnectedToOldPlatform::getStationName))
            .publish(StationConnectedToCsc.class, "station-configuration", MessagesKeyProvider.withTypeStationConnected(StationConnectedToCsc::getStationName))
            .publish(StationCreated.class, "emobility-station-StationEvents", MessagesKeyProvider.withType(StationCreated::getStationName))
            .publish(StationDeleted.class, "emobility-station-StationEvents", MessagesKeyProvider.withType(StationDeleted::getStationName))
            .publish(StationSnapshotV1.class, "station-configuration-station-snapshot-v1", StationSnapshotV1::getStationName)
            .build();

    private static Map<Class<?>, Type> mapping;

    public static Type of(DomainEvent event) {
        return of(event.getClass());
    }

    public static Type of(Class<? extends DomainEvent> type) {
        return mapping.get(type);
    }

    public static boolean hasTypeName(Class<? extends DomainEvent> type, String typeName) {
        return EventTypes.of(type).getType().equals(typeName);
    }

    @Value
    public static class Type {
        String type;
        String version;

        public static Type of(String typeName) {
            String[] parts = typeName.split("_v");
            if (parts.length != 2 || StringUtils.isBlank(parts[1])) {
                throw new IllegalArgumentException(
                        "Version required in " + DomainEvent.class.getName() + " JsonSubTypes name, like StationProtocolChanged_v1, '_v' part is important, thrown for type name: " + typeName
                );
            }
            return new Type(parts[0], parts[1]);
        }
    }

    static void init(Map<Class<?>, Type> mapping) {
        EventTypes.mapping = mapping;
    }
}
