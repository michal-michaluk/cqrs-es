package devices.configuration.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.remote.IntervalRules;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IntervalRulesRepository {

    private final FeaturesConfigurationRepository repository;
    private final ObjectMapper mapper;

    public IntervalRules get() {
        return repository.findByName("IntervalRules")
                .map(FeaturesConfigurationEntity::getConfiguration)
                .map(this::parse)
                .orElse(IntervalRules.defaultRule());
    }

    @SneakyThrows
    private IntervalRules parse(String json) {
        return mapper.readValue(json, IntervalRules.class);
    }
}
