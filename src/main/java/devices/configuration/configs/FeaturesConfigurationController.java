package devices.configuration.configs;

import devices.configuration.remote.IntervalRules;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FeaturesConfigurationController {

    private final FeaturesConfigurationRepository repository;

    @GetMapping(path = "/configs/{config}")
    public IntervalRules get(@PathVariable String config) {
        return repository.findFirst1ByNameOrderByTimeDesc(config)
                .map(FeaturesConfigurationEntity::getConfiguration)
                .orElseGet(IntervalRules::defaultRules);
    }
}
