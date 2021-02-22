package devices.configuration.features.bootNotification;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StationsCatalogueEntityTest {

    private static final String STATION_NAME = "some station";
    private static final String CSC = "charging-station-communication";

    @Test
    void Should_produce_StationProtocolChanged_event_when_setting_new_protocol_was_applied() {
        //given
        final String protocolVersion = "protocolVersion";
        final String protocolName =  "protocolName";
        final String mediaType = "mediaType";
        StationsCatalogueEntity stationsCatalogueEntity = new StationsCatalogueEntity(STATION_NAME);

        //when
        stationsCatalogueEntity.applyNewProtocol(protocolName, protocolVersion, mediaType);

        //then
        assertThat(stationsCatalogueEntity.getEvents()).hasSize(1);
        assertThat(stationsCatalogueEntity.getEvents()).anySatisfy(stationEvent ->
                assertThat(stationEvent).isInstanceOfSatisfying(StationProtocolChanged.class, stationProtocolChanged -> {
                    assertThat(stationProtocolChanged.getStationName()).isEqualTo(STATION_NAME);
                    assertThat(stationProtocolChanged.getProtocolName()).isEqualTo(protocolName);
                    assertThat(stationProtocolChanged.getProtocolVersion()).isEqualTo(protocolVersion);
                    assertThat(stationProtocolChanged.getMediaType()).isEqualTo(mediaType);
                }));
    }

    @Test
    void should_create_StationConnectedToCsc_event_after_new_csms_was_applied() {
        // given
        StationsCatalogueEntity stationsCatalogueEntity = new StationsCatalogueEntity(STATION_NAME);

        // when
        stationsCatalogueEntity.applyNewCsms(CSC);

        assertThat(stationsCatalogueEntity.getEvents()).hasSize(1);
        assertThat(stationsCatalogueEntity.getEvents()).anySatisfy(stationEvent ->
                assertThat(stationEvent).isInstanceOfSatisfying(StationConnectedToCsc.class, stationConnectedToCsc ->
                        assertThat(stationConnectedToCsc.getStationName()).isEqualTo(STATION_NAME)));
    }
}
