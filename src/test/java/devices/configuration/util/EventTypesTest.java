package devices.configuration.util;

import devices.configuration.EventTypes;
import devices.configuration.IntegrationTest;
import devices.configuration.legacy.StationLocationUpdated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class EventTypesTest {

    @Test
    void Should_resolve_all_types_and_versions_or_throw() {
        assertThat(EventTypes.of(StationLocationUpdated.class))
                .isEqualTo(new EventTypes.Type("StationLocationUpdated", "1"));
    }
}
