package devices.configuration.configs;

import devices.configuration.remote.IntervalRules;

public interface IntervalRulesRepository {
    IntervalRules get();

    void save(IntervalRules object);
}
