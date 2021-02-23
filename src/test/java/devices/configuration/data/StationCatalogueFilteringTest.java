package devices.configuration.data;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@IntegrationTest
class StationCatalogueFilteringTest {

    @Autowired
    RequestFixture requestFixture;

    @Autowired
    DeviceRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    public void Should_filter_stations_by_name_like_when_one_matches() {
        // given
        String abcd = "abcd";
        String cdef = "cdef";

        Device station1 = StationsFixture.evb(abcd);
        Device station2 = StationsFixture.evb(cdef);

        repository.save(station1);
        repository.save(station2);

        // when
        ResponseEntity<String> response = requestFixture.getStationsWithNameLike("bc");

        // then
        StationsResponseAssert.assertThat(response)
                .hasStationWithNameInBody(abcd)
                .hasSize(1);
    }

    @Test
    public void Should_filter_stations_by_name_like_ignoring_case() {
        // given
        String abcd = "ABCD";
        String cdef = "cdef";

        Device station1 = StationsFixture.evb(abcd);
        Device station2 = StationsFixture.evb(cdef);

        repository.save(station1);
        repository.save(station2);

        // when
        ResponseEntity<String> response2 = requestFixture.getStationsWithNameLike("Cd");

        // then
        StationsResponseAssert.assertThat(response2)
                .hasStationWithNameInBody(abcd)
                .hasStationWithNameInBody(cdef)
                .hasSize(2);
    }

    @Test
    public void Should_filter_stations_by_name_like_when_criteria_is_null() {
        // given
        String abcd = "abcd";
        String cdef = "cdef";

        Device station1 = StationsFixture.evb(abcd);
        Device station2 = StationsFixture.evb(cdef);

        repository.save(station1);
        repository.save(station2);

        // when
        ResponseEntity<String> response = requestFixture.getStationsWithNameLike(null);

        // then
        StationsResponseAssert.assertThat(response)
                .hasStationWithNameInBody(abcd)
                .hasStationWithNameInBody(cdef)
                .hasSize(2);
    }
}
