package devices.configuration.configs;

import devices.configuration.IntegrationTest;
import devices.configuration.JsonAssert;
import devices.configuration.remote.IntervalRulesFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static devices.configuration.TestTransaction.transactional;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
class FeaturesConfigurationRepositoryTest {

    @Autowired
    private FeaturesConfigurationRepository repository;

    @Test
    public void shouldSaveAndLoadIntervalRules() {
        //given
        String name = "IntervalRules";
        String json = JsonAssert.json(IntervalRulesFixture.currentRules());
        FeaturesConfigurationEntity entity = FeaturesConfigurationFixture.entity(name, json);

        // when
        transactional(() -> repository.save(entity));
        var result = transactional(() -> repository.findByName(name));

        // then
        assertThat(result).hasValueSatisfying(e ->
                JsonAssert.assertThat(e.getConfiguration()).isExactlyLike(json));
    }
}