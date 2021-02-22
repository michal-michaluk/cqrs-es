package devices.configuration.features.bootNotification;

import devices.configuration.features.bootNotification.protocol.EggplantProtocolName;

class BootNotificationFieldsMapper {
    static BootNotificationFields map(StationsCatalogueEntity stationsCatalogueEntity) {
        if (stationsCatalogueEntity == null) {
            return null;
        }

        final EggplantProtocolName eggplantProtocolName = EggplantProtocolName.fromCsms(stationsCatalogueEntity.getCsms(),
                stationsCatalogueEntity.getMediaType());

        return new BootNotificationFields(stationsCatalogueEntity.getSoftwareVersion(),
                eggplantProtocolName.getName().toUpperCase(), stationsCatalogueEntity.getProtocolVersion());
    }
}
