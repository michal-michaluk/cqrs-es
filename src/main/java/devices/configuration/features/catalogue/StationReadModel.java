package devices.configuration.features.catalogue;

import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.location.OpeningHours;
import devices.configuration.features.catalogue.photo.StationPhoto;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class StationReadModel {

    String name;
    String physicalReference;
    String customName;
    OwnershipSnapshot ownership;
    LocationSnapshot location;
    OpeningHours openingTimes;
    Settings.Visibility visibility;
    List<ConnectorSnapshot> connectors;
    List<Image> images;
    StationValidation validations;
    Settings settings;

    public static StationReadModel from(Station station) {
        return StationReadModel.builder()
                .name(station.getName())
                .physicalReference(station.getPhysicalReference())
                .customName(station.getCustomName())
                .ownership(OwnershipSnapshot.from(station.getOwnership()))
                .location(LocationSnapshot.from(station.getLocation()))
                .openingTimes(station.getOpeningHours())
                .visibility(station.getVisibility())
                .connectors(ConnectorSnapshot.from(station.getConnectors()))
                .images(Image.from(station.getPhotos()))
                .validations(station.validate())
                .settings(station.getSettings())
                .build();
    }

    @Value
    @Builder
    public static class ListItem {

        String name;
        String physicalReference;
        String customName;
        LocationSnapshot location;
        List<ConnectorSnapshot> connectors;

        public static ListItem from(Station station) {
            return ListItem.builder()
                    .name(station.getName())
                    .physicalReference(station.getPhysicalReference())
                    .customName(station.getCustomName())
                    .location(LocationSnapshot.from(station.getLocation()))
                    .connectors(ConnectorSnapshot.from(station.getConnectors()))
                    .build();
        }
    }

    @Value
    @Builder
    static class LocationSnapshot {
        String street;
        String houseNumber;
        String city;
        String postalCode;
        String state;
        String country;
        CoordinatesSnapshot coordinates;

        public static LocationSnapshot from(Location location) {
            if (location == null) return null;
            return LocationSnapshot.builder()
                    .street(location.getStreet())
                    .houseNumber(location.getHouseNumber())
                    .city(location.getCity())
                    .postalCode(location.getPostalCode())
                    .state(location.getState())
                    .country(location.getCountryISO())
                    .coordinates(CoordinatesSnapshot.builder()
                            .longitude(new BigDecimal(location.getCoordinates().getLongitude()))
                            .latitude(new BigDecimal(location.getCoordinates().getLatitude()))
                            .build()
                    )
                    .build();
        }

        @Value
        @Builder
        static class CoordinatesSnapshot {
            BigDecimal longitude;
            BigDecimal latitude;
        }
    }

    @Value
    @Builder
    static class OwnershipSnapshot {
        String cpo;
        String lcp;

        static OwnershipSnapshot from(Ownership ownership) {
            return OwnershipSnapshot.builder()
                    .cpo(ownership.getCpo())
                    .lcp(ownership.getLcp())
                    .build();
        }
    }

    enum ConnectorTypeSnapshot {
        CHADEMO,
        IEC_62196_T1,
        IEC_62196_T2,
        IEC_62196_T2_COMBO;

        static ConnectorTypeSnapshot from(ConnectorType type) {
            switch (type) {
                case CHADEMO:
                    return CHADEMO;
                case TYPE_1_SAE_J1772:
                    return IEC_62196_T1;
                case TYPE_2_MENNEKES:
                    return IEC_62196_T2;
                case TYPE_2_COMBO:
                    return IEC_62196_T2_COMBO;
            }
            throw new IllegalStateException("" + type);
        }
    }

    @Value
    @Builder
    static class ConnectorSnapshot {
        String name;
        String evseId;
        ConnectorTypeSnapshot standard;
        Format format;
        PowerType powerType;
        String advertisedPower;
        BigDecimal maxVoltage;
        BigDecimal maxAmperage;
        BigDecimal maxElectricPower;

        static List<ConnectorSnapshot> from(List<Connector> connectors) {
            return connectors.stream()
                    .sorted(Comparator.comparing(Connector::getOcppConnectorId))
                    .map(connector -> ConnectorSnapshot.builder()
                            .name(connector.getName())
                            .evseId(connector.getEvseId())
                            .standard(ConnectorTypeSnapshot.from(connector.getType()))
                            .format(connector.getFormat())
                            .powerType(connector.getPowerType())
                            .advertisedPower(connector.getAdvertisedPower())
                            .maxVoltage(new BigDecimal(connector.getVoltage()))
                            .maxAmperage(new BigDecimal(connector.getAmps()))
                            .maxElectricPower(connector.getPower())
                            .build())
                    .collect(Collectors.toList());
        }
    }

    @Value
    static class Image {
        URL url;
        String category;

        static List<Image> from(List<StationPhoto> photos) {
            return photos.stream()
                    .map(photo -> new Image(photo.getUrl(), photo.getCategory().toUpperCase()))
                    .collect(Collectors.toList());
        }
    }
}
