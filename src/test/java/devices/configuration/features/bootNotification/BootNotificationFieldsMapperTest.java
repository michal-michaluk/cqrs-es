package devices.configuration.features.bootNotification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BootNotificationFieldsMapperTest {

    @Test
    public void ShouldReturnNullWhenEntityIsNotPresent() {
        // when && then
        assertThat(BootNotificationFieldsMapper.map(null)).isNull();
    }

    @Test
    public void ShouldParseCsmsCSCAsOcppAzure() {
        // given
        StationsCatalogueEntity stationsCatalogueEntity = createStationCatalogue("charging-station-communication", "json");

        // when
        BootNotificationFields bootNotificationFields = BootNotificationFieldsMapper.map(stationsCatalogueEntity);

        // then
        assertThat(bootNotificationFields).isNotNull();
        assertThat(bootNotificationFields.getProtocolName()).isEqualTo("OCPP-AZURE");
    }

    @Test
    public void ShouldParseCsmsEmobilityAsOcpp() {
        // given
        StationsCatalogueEntity stationsCatalogueEntity = createStationCatalogue("emobility", "soap");

        // when
        BootNotificationFields bootNotificationFields = BootNotificationFieldsMapper.map(stationsCatalogueEntity);

        // then
        assertThat(bootNotificationFields).isNotNull();
        assertThat(bootNotificationFields.getProtocolName()).isEqualTo("OCPP");
    }

    @Test
    public void ShouldMapSoftwareVersion() {
        // given
        StationsCatalogueEntity stationsCatalogueEntity = createStationCatalogue("emobility", "soap");

        // when
        BootNotificationFields bootNotificationFields = BootNotificationFieldsMapper.map(stationsCatalogueEntity);

        // then
        assertThat(bootNotificationFields).isNotNull();
        assertThat(bootNotificationFields.getSoftwareVersion()).isEqualTo("softwareVersion");
    }

    @Test
    public void ShouldParseCsmsEmobilityAndMediaJsonAsOcppJ() {
        // given
        StationsCatalogueEntity stationsCatalogueEntity = createStationCatalogue("emobility", "json");

        // when
        BootNotificationFields bootNotificationFields = BootNotificationFieldsMapper.map(stationsCatalogueEntity);

        // then
        assertThat(bootNotificationFields).isNotNull();
        assertThat(bootNotificationFields.getProtocolName()).isEqualTo("OCPP-J");
    }

    private StationsCatalogueEntity createStationCatalogue(String csms, String mediaType) {
        StationsCatalogueEntity stationsCatalogueEntity = new StationsCatalogueEntity("any");
        stationsCatalogueEntity.setCsms(csms);
        stationsCatalogueEntity.setMediaType(mediaType);
        stationsCatalogueEntity.setProtocolVersion("1.6");
        stationsCatalogueEntity.setSoftwareVersion("softwareVersion");
        return stationsCatalogueEntity;
    }

}
