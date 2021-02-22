package devices.configuration.outbox;

import devices.configuration.DomainEvent;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OutboxConfiguration {

    private final Map<Class<? extends DomainEvent>, Config> config;

    private OutboxConfiguration(Map<Class<? extends DomainEvent>, Config> config) {
        this.config = config;
    }

    public static OutboxConfigurationBuilder builder() {
        return new OutboxConfigurationBuilder();
    }

    Config ofEvent(DomainEvent event) {
        return ofEvent(event.getClass());
    }

    Config ofEvent(Class<? extends DomainEvent> type) {
        return config.get(type);
    }

    boolean definedFor(DomainEvent event) {
        return config.containsKey(event.getClass());
    }

    @Value
    static class Config {
        String topic;
        Function<DomainEvent, String> partition;

        private static <T extends DomainEvent> Map.Entry<Class<T>, Config> of(
                Class<T> type, String topic, Function<T, String> partition) {
            return Map.entry(type, new Config(topic, (DomainEvent e) -> partition.apply(type.cast(e))));
        }

        String partitionKey(DomainEvent payload) {
            return partition.apply(payload);
        }
    }

    public static class OutboxConfigurationBuilder {

        private final Map<Class<? extends DomainEvent>, Config> config = new HashMap<>();

        public <T extends DomainEvent> OutboxConfigurationBuilder publish(Class<T> type, String topic, Function<T, String> partition) {
            Map.Entry<Class<T>, Config> entry = Config.of(type, topic, partition);
            config.put(entry.getKey(), entry.getValue());
            return this;
        }

        public OutboxConfiguration build() {
            return new OutboxConfiguration(Map.copyOf(config));
        }
    }
}
