package devices.configuration.configs

import devices.configuration.IntegrationTest
import devices.configuration.JsonAssert
import devices.configuration.remote.IntervalRules
import devices.configuration.remote.IntervalRulesFixture
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import javax.transaction.Transactional
import java.util.function.Supplier

import static devices.configuration.TestTransaction.transactional

@IntegrationTest
@Transactional
class IntervalRulesRepositoryTest extends Specification {

    @Autowired
    private IntervalRulesRepository repository
    @Autowired
    private FeaturesConfigurationRepository db

    def "Should fetch persisted rules"() {
        given:
        transactional { db.save(currentRules()) }

        when:
        IntervalRules actual = transactional({ repository.get() } as Supplier)

        then:
        JsonAssert.assertThat(actual)
                .hasFieldsLike(IntervalRulesFixture.specifiedRules())
    }

    def "Should fetch default rules when non is persisted"() {
        given:
        transactional { noRules() }

        when:
        IntervalRules actual = transactional({ repository.get() } as Supplier)

        then:
        JsonAssert.assertThat(actual)
                .hasFieldsLike(IntervalRules.defaultRules())
    }

    def currentRules() {
        new FeaturesConfigurationEntity(
                IntervalRulesRepository.INTERVAL_RULES,
                IntervalRulesFixture.specifiedRules()
        )
    }

    void noRules() {
        db.deleteAll()
    }
}
