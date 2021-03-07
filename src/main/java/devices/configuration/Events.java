package devices.configuration;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class Events {
    private final Map<? extends Class<? extends DomainEvent>, DomainEvent> lastEvents;

    public static Events groupByClass(Stream<DomainEvent> events) {
        return new Events(events.collect(Collectors.toUnmodifiableMap(
                DomainEvent::getClass,
                Function.identity()
        )));
    }

    public <T extends DomainEvent> Optional<T> getLast(Class<T> type) {
        DomainEvent event = lastEvents.getOrDefault(type, null);
        if (type.isInstance(event)) {
            return Optional.of(type.cast(event));
        } else {
            return Optional.empty();
        }
    }
}
