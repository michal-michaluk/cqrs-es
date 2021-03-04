package devices.configuration.configs;

import devices.configuration.remote.IntervalRules;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IntervalRulesRepository {
    static final String INTERVAL_RULES = "IntervalRules";

    private final FeaturesConfigurationRepository repository;

    public IntervalRules get() {
        return repository.findByName(INTERVAL_RULES)
                .map(FeaturesConfigurationEntity::getConfiguration)
                .orElse(IntervalRules.defaultRules());
    }
}
