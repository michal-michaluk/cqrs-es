package devices.configuration.util;

import devices.configuration.EventTypes;
import devices.configuration.IntegrationTest;
import devices.configuration.published.StationSnapshotV1;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class EventTypesTest {

    @Test
    void Should_resolve_all_types_and_versions_or_throw() {
        assertThat(EventTypes.of(StationSnapshotV1.class))
                .isEqualTo(new EventTypes.Type("StationSnapshot", "1"));
    }
}
