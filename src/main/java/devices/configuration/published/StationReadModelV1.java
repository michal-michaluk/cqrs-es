package devices.configuration.published;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import devices.configuration.data.*;
import devices.configuration.data.location.Location;
import devices.configuration.data.location.OpeningHours;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
public class StationReadModelV1 {

    String name;
    String ocppChargePoint;
    String physicalReference;
    OwnershipSnapshot ownership;
    LocationSnapshot location;
    OpeningTimesSnapshot openingTimes;
    VisibilitySnapshot visibility;
    List<PointSnapshot> points;
    @JsonProperty("experimental-validations")
    StationValidation validations;
    @JsonProperty("experimental-settings")
    Settings settings;

    public static StationReadModelV1 from(Device station) {
        return StationReadModelV1.builder()
                .name(station.getName())
                .ocppChargePoint(station.getName())
                .physicalReference(station.getPhysicalReference())
                .ownership(OwnershipSnapshot.from(station.getOwnership()))
                .location(LocationSnapshot.from(station.getLocation()))
                .openingTimes(OpeningTimesSnapshot.from(station.getOpeningHours()))
                .visibility(VisibilitySnapshot.from(station.getVisibility()))
                .points(PointSnapshot.from(station.getConnectors()))
                .validations(station.validate())
                .settings(station.getSettings())
                .build();
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

    @Value
    @Builder
    static class VisibilitySnapshot {
        InMobileApp mobileApp;
        boolean roamingEnabled;

        enum InMobileApp {USABLE_AND_VISIBLE_ON_MAP, USABLE_BUT_HIDDEN_ON_MAP, INACCESSIBLE_AND_HIDDEN_ON_MAP}

        static VisibilitySnapshot from(Settings.Visibility visibility) {
            return VisibilitySnapshot.builder()
                    .mobileApp(map(visibility.getMobileApp()))
                    .roamingEnabled(visibility.isRoamingEnabled())
                    .build();
        }

        private static InMobileApp map(Settings.Visibility.AppVisibility visibility) {
            switch (visibility) {
                case USABLE_AND_VISIBLE_ON_MAP:
                    return InMobileApp.USABLE_AND_VISIBLE_ON_MAP;
                case USABLE_BUT_HIDDEN_ON_MAP:
                    return InMobileApp.USABLE_BUT_HIDDEN_ON_MAP;
                case INACCESSIBLE_AND_HIDDEN_ON_MAP:
                    return InMobileApp.INACCESSIBLE_AND_HIDDEN_ON_MAP;
            }
            throw new IllegalArgumentException("Not supported AppVisibility " + visibility);
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

    enum FormatSnapshot {
        SOCKET, CABLE;

        static FormatSnapshot from(Format format) {
            switch (format) {
                case SOCKET:
                    return SOCKET;
                case CABLE:
                    return CABLE;
            }
            throw new IllegalStateException("" + format);
        }
    }

    enum PowerTypeSnapshot {
        AC_1_PHASE, AC_3_PHASE, DC;

        static PowerTypeSnapshot from(boolean isAc, Integer phases) {
            if (!isAc) {
                return DC;
            } else if (phases == 1) {
                return AC_1_PHASE;
            } else if (phases == 3) {
                return AC_3_PHASE;
            } else {
                throw new IllegalStateException("isAC " + isAc + " phases " + phases);
            }
        }
    }

    @Value
    @Builder
    static class PointSnapshot {
        String name;
        int ocppConnectorId;
        String physicalReference;
        String evseId;
        ConnectorTypeSnapshot standard;
        FormatSnapshot format;
        PowerTypeSnapshot powerType;
        String advertisedPower;
        BigDecimal maxVoltage;
        BigDecimal maxAmperage;
        BigDecimal maxElectricPower;

        static List<PointSnapshot> from(List<Connector> connectors) {
            return connectors.stream()
                    .sorted(Comparator.comparing(Connector::getOcppConnectorId))
                    .map(connector -> PointSnapshot.builder()
                            .name(connector.getName())
                            .ocppConnectorId(connector.getOcppConnectorId())
                            .physicalReference(connector.getPhysicalReference())
                            .evseId(connector.getEvseId())
                            .standard(ConnectorTypeSnapshot.from(connector.getType()))
                            .format(FormatSnapshot.from(connector.getFormat()))
                            .powerType(PowerTypeSnapshot.from(connector.isAc(), connector.getPhases()))
                            .advertisedPower(connector.getAdvertisedPower())
                            .maxVoltage(new BigDecimal(connector.getVoltage()))
                            .maxAmperage(new BigDecimal(connector.getAmps()))
                            .maxElectricPower(connector.getPower())
                            .build())
                    .collect(Collectors.toList());
        }
    }

    @Value
    static class OpeningTimesSnapshot {
        boolean twentyFourSeven;
        @JsonIgnore
        WeekSnapshot opened; // regularHours

        @Value
        static class WeekSnapshot {
            OpeningTimeSnapshot monday;
            OpeningTimeSnapshot tuesday;
            OpeningTimeSnapshot wednesday;
            OpeningTimeSnapshot thursday;
            OpeningTimeSnapshot friday;
            OpeningTimeSnapshot saturday;
            OpeningTimeSnapshot sunday;
        }

        @Value
        static class OpeningTimeSnapshot {
            boolean open24h;
            boolean closed;
            @JsonInclude(NON_NULL)
            @JsonSerialize(using = LocalTimeSerializer.class)
            LocalTime open;
            @JsonInclude(NON_NULL)
            @JsonSerialize(using = LocalTimeSerializer.class)
            LocalTime close;

            static OpeningTimeSnapshot from(OpeningHours.OpeningTime day) {
                if (day == null || day.isOpen24h()) {
                    return new OpeningTimeSnapshot(true, false, null, null);
                } else if (day.isClosed()) {
                    return new OpeningTimeSnapshot(false, true, null, null);
                } else {
                    Objects.requireNonNull(day.getOpen());
                    Objects.requireNonNull(day.getClose());
                    return new OpeningTimeSnapshot(false, false, day.getOpen(), day.getClose());
                }
            }
        }

        static OpeningTimesSnapshot from(OpeningHours openingHours) {
            if (openingHours == null || openingHours.isAlwaysOpen() || openingHours.getOpened() == null) {
                return new OpeningTimesSnapshot(true, null);
            }
            return new OpeningTimesSnapshot(false, new WeekSnapshot(
                    OpeningTimeSnapshot.from(openingHours.getOpened().getMonday()),
                    OpeningTimeSnapshot.from(openingHours.getOpened().getTuesday()),
                    OpeningTimeSnapshot.from(openingHours.getOpened().getWednesday()),
                    OpeningTimeSnapshot.from(openingHours.getOpened().getThursday()),
                    OpeningTimeSnapshot.from(openingHours.getOpened().getFriday()),
                    OpeningTimeSnapshot.from(openingHours.getOpened().getSaturday()),
                    OpeningTimeSnapshot.from(openingHours.getOpened().getSunday())
            ));
        }
    }
}
