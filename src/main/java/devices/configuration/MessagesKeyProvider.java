package devices.configuration;

import java.util.function.Function;

class MessagesKeyProvider {

    public static <T extends DomainEvent> Function<T, String> withType(Function<T, String> base) {
        return event -> base.apply(event) + "_" + EventTypes.of(event).getType();
    }

    public static <T extends DomainEvent> Function<T, String> withTypeStationConnected(Function<T, String> base) {
        return event -> base.apply(event) + "_StationConnected";
    }
}
