package devices.configuration.configs;

import devices.configuration.remote.IntervalRules;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class IntervalRulesDocumentRepository implements IntervalRulesRepository {
    static final String INTERVAL_RULES = "IntervalRules";

    private final Clock clock;
    private final FeaturesConfigurationRepository repository;

    public IntervalRules get() {
        return repository.findFirst1ByNameOrderByTimeDesc(INTERVAL_RULES)
                .map(FeaturesConfigurationEntity::getConfiguration)
                .orElse(IntervalRules.defaultRules());
    }

    public void save(IntervalRules object) {
        repository.save(new FeaturesConfigurationEntity(
                UUID.randomUUID(),
                Instant.now(clock),
                INTERVAL_RULES, object)
        );
    }
}