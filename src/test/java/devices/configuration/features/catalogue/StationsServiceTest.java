package devices.configuration.features.catalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StationsServiceTest {

    private StationsService stationsService;
    private StationImageProperties imageProperties;

    @BeforeEach
    void setupSystemUnderTest() {
        StationsRepository stationsRepository = mock(StationsRepository.class);
        StationImageProperties stationImageProperties = new StationImageProperties();
        stationImageProperties.setId(UUID.randomUUID().toString());
        imageProperties = Mockito.spy(stationImageProperties);
        stationsService = new StationsService(stationsRepository, new StationFactory(imageProperties));
    }

    @Test
    void Should_create_new_station_via_update() {
        //given
        String newStationName = UUID.randomUUID().toString();

        //when
        Station resp = stationsService.updateStation(
                newStationName,
                new StationUpdate().setLocation(StationsFixture.Locations.rooseveltlaanInGent())
        );

        //then
        assertThat(resp).isNotNull()
                .extracting(Station::getName, Station::getImageId, Station::getLocation)
                .containsExactly(newStationName, imageProperties.getDefaultImageId().getId(), StationsFixture.Locations.rooseveltlaanInGent());
        verify(imageProperties).getId();
    }
}
