package devices.configuration.legacy.stationImport;

import com.google.common.collect.Lists;
import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class StationView {

    @Value
    static class Lcp {
        Long id;
        String name;
        String lcpUserName;
        Long cpoId;
        String sapId;
    }

    @Value
    static class Party {
        Long id;
        String name;
        boolean cpo;
        boolean emsp;
        boolean supplier;
        String partyName;
    }

    @Value
    @Builder
    public static class ChargingStation {
        Long id;
        String name;
        ChargingStationType chargingStationType;
        List<ChargingPoint> chargingPoints;
        LocationView location;
        List<OpeningTimes> openingTimes;
        Boolean deleted;
        Date yearOfInstallation;
        String forProject;
        Boolean billing;
        Group group;
        Boolean automatedWhitelistUpdates;
        Boolean connectedExternally;
        PublicInfo publicInfo;
        String softwareVersion;
        SimCard simCard;
        String xmlRpcVendor;
        String xmlRpcVersion;
        String referenceId;
        Boolean showOnMap;
        Boolean reimbursement;
        Boolean autoStart;
        Boolean remoteControl;
        Boolean automaticGeoLocation;
        Boolean nonDiscriminatoryAccess;
        String priceInformation;
        String chargingStationDescription;
        String status;
        String nobilId;
        Party cpo;
        Lcp lcp;
        Long basketId;
        String stationConnectivityURL;
        CSCState cscState;
        Object llmcluster;
        Boolean automaticUpdateOfGeoLocationAllowed;
        String telNumber;
        List<String> tags;

        public List<Connector> toConnectors() {
            if (chargingPoints == null) {
                return List.of();
            }
            return chargingPoints.stream()
                    .map(ChargingPoint::toConnector)
                    .collect(Collectors.toList());
        }

        private Boolean getAccessibility() {
            if (chargingPoints == null) {
                return false;
            }
            return chargingPoints.stream()
                    .map(ChargingPoint::getAccessibility)
                    .map(ac -> ac == Accessibility.SEMIPUBLIC ? Accessibility.PUBLIC : ac)
                    .collect(Collectors.toSet())
                    .contains(Accessibility.PUBLIC);
        }

        public Ownership getOwnership() {
            return new Ownership(
                    Optional.ofNullable(cpo).map(Party::getName).orElse(null),
                    Optional.ofNullable(lcp).map(Lcp::getName).orElse(null)
            );
        }

        public Settings getSettings() {
            return Settings.builder()
                    .autoStart(autoStart)
                    .remoteControl(remoteControl)
                    .billing(billing)
                    .reimbursement(reimbursement)
                    .showOnMap(showOnMap)
                    .publicAccess(getAccessibility())
                    .build();
        }
    }

    @Value
    static class ChargingStationType {
        Long id;
        String name;
        String manufacturer;
        Boolean remoteControlForELCB;
        Boolean supportedMasterSlave;
        Boolean canUseWSAddressing;
        String triggerInterfaceUrl;
        Boolean supportedTriggerInterface;
        Boolean deleted;
        List<ChargingStationTypeFile> files;
        Set<String> triggers;
        String description;
        Long code;
    }

    @Value
    static class ChargingStationTypeFile {
        Long id;
        byte[] data;
        String fileName;
        String contentType;
        Long sequence;
        ChargingStationType chargingStationType;
    }

    @Value
    public static class ChargingPoint {
        Long id;
        String evseid;
        ChargingPointType chargingPointType;
        Long position;
        Integer connectorId;
        String name;
        BigDecimal meterData;
        String status;
        Date modDate;
        Boolean monitoring;
        String softwareVersion;
        Boolean deleted;
        Object llmCluster;
        Date lastStatusUpdate;
        String errorProcessingState;
        Object lastMonitoringLog;
        List<Labelcode> labelCodes;
        Price mopasPrice;
        Accessibility accessibility;
        String eichrechtPublicKey;
        ChargingPointId chargingPointId;
        String accessibilityName;

        Connector toConnector() {
            List<PlugType> plugTypes = chargingPointType.plugTypes.stream()
                    .filter(plugType -> !plugType.name.contains("DO NOT CLICK"))
                    .collect(Collectors.toList());
            if (plugTypes.size() != 1) {
                throw new IllegalStateException("expecting exactly one plug type but got: " + plugTypes + " after filtering out 'DO NOT CLICK'");
            }
            PlugType plugType = plugTypes.get(0);
            ConnectorType connectorType = mapConnectorType(plugType.name);
            ChargingPointTypeMapping type = ChargingPointTypeMapping.mappingFor(chargingPointType.name, evseid);
            validatePower(type, type.getPowerType().getPhase());

            Connector connector = new Connector()
                    .setName(name)
                    .setOcppConnectorId(connectorId)
                    .setPhysicalReference(null)
                    .setEvseId(evseid)
                    .setType(connectorType)
                    .setFormat(type.getConnectorFormat())
                    .setVoltage(calculateAc(connectorType) ? "230.0" : toStringIfNotNull(chargingPointType.ratingsType.nominalCurrent))
                    .setAmps(toStringIfNotNull(chargingPointType.ratingsType.nominalCurrent))
                    .setPower(chargingPointType.ratingsType.maximumPower)
                    .setPhases(type.getPowerType().getPhaseAsString())
                    .setAc(calculateAc(connectorType))
                    .setDc(calculateDc(connectorType));

            return connector.setLegacy(Map.ofEntries(
                    Map.entry("type", chargingPointType.name),
                    Map.entry("ratingId", chargingPointType.ratingsType.id),
                    Map.entry("ratingMaximumPower", chargingPointType.ratingsType.maximumPower),
                    Map.entry("ratingGuaranteedPower", chargingPointType.ratingsType.guaranteedPower),
                    Map.entry("ratingNominalVoltage", chargingPointType.ratingsType.nominalVoltage),
                    Map.entry("ratingNominalCurrent", chargingPointType.ratingsType.nominalCurrent),
                    Map.entry("ateamPointType", type.getPointType()),
                    Map.entry("ateamConnectorType", type.getConnectorType()),
                    Map.entry("ateamConnectorFormat", type.getConnectorFormat()),
                    Map.entry("ateamPowerType", type.getPowerType().toString()),
                    Map.entry("ateamPower", type.getPower()),
                    Map.entry("calculatedPower", connector.getPowerInWatts()),
                    Map.entry("advertisedPower", connector.getAdvertisedPower())
            ));
        }

        private void validatePower(ChargingPointTypeMapping type, Integer phases) {
            BigDecimal calculated = chargingPointType.ratingsType.calculatePower(phases);
            BigDecimal rounded = calculated.divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP)
                    .round(new MathContext(2, RoundingMode.HALF_UP))
                    .stripTrailingZeros();
            if (type.getPower() != null && !type.getPower().toPlainString().equals(rounded.toPlainString())) {
                log.warn("Rounded power {} for evseid {} expected to be to {}, but was calculated as {} = {} * {} * {} / 1000",
                        rounded.toPlainString(), evseid, type.getPower().toPlainString(),
                        calculated.toPlainString(),
                        chargingPointType.ratingsType.getNominalVoltage().toPlainString(),
                        chargingPointType.ratingsType.getNominalCurrent().toPlainString(),
                        phases
                );
            }
        }

        private ConnectorType mapConnectorType(String name) {
            ConnectorType connectorType = Map.ofEntries(
                    Map.entry("ATTACHED_CABLE_TYPE_1_MODE_3", ConnectorType.TYPE_1_SAE_J1772),
                    Map.entry("ATTACHED_CABLE_TYPE_2_MODE_3", ConnectorType.TYPE_2_MENNEKES),
                    Map.entry("CCS", ConnectorType.TYPE_2_COMBO),
                    Map.entry("CHADEMO", ConnectorType.CHADEMO),
                    Map.entry("TYPE_2_MODE_3", ConnectorType.TYPE_2_MENNEKES)
            ).get(name);
            if (connectorType == null) {
                throw new IllegalStateException("Given plugType: " + name + " is not supported");
            }
            return connectorType;
        }

        private boolean calculateAc(ConnectorType mapped) {
            return Set.of(ConnectorType.TYPE_1_SAE_J1772, ConnectorType.TYPE_2_MENNEKES).contains(mapped);
        }

        private boolean calculateDc(ConnectorType mapped) {
            return Set.of(ConnectorType.CHADEMO, ConnectorType.TYPE_2_COMBO).contains(mapped);
        }

        private String toStringIfNotNull(BigDecimal value) {
            return value != null ? value.toPlainString() : null;
        }
    }

    @Value
    static class ChargingPointId {
        String chargingStationId;
        String chargingPointId;
        String chargingStationIdAsByteArray;
        String chargingPointIdAsByteArray;
    }

    @Value
    static class ChargingPointType {
        Long id;
        String name;
        List<PlugType> plugTypes;
        RatingsType ratingsType;
        String chargingType;
        Boolean deleted;
    }

    @Value
    static class PlugType {
        Long id;
        String name;
        String format;
        Long code;
        String note;
    }

    @Value
    static class RatingsType {
        Long id;
        BigDecimal maximumPower;
        BigDecimal guaranteedPower;
        BigDecimal nominalVoltage;
        BigDecimal nominalCurrent;

        BigDecimal calculatePower(Integer phase) {
            if (phase == null) {
                return nominalVoltage.multiply(nominalCurrent);
            } else {
                return new BigDecimal("230")
                        .multiply(nominalCurrent)
                        .multiply(BigDecimal.valueOf(phase));
            }
        }
    }

    enum Accessibility {
        PRIVATE,
        PUBLIC,
        SEMIPUBLIC
    }

    @Value
    static class Labelcode {
        Long id;
        String labelcode;
        String provider;
    }

    @Value
    static class Price {
        Long priceId;
        String priceUnit;
        Double amountPerUnit;
        String name;
        String currency;
        String country;
        Boolean defaultPrice;
        String priceStatus;
        Date updateTime;
    }

    @Value
    static class LocationView {
        Long id;
        String city;
        String zipcode;
        String street;
        String houseNumber;
        String note1;
        String note2;
        String district;
        Boolean deleted;
        BigDecimal longitude;
        BigDecimal latitude;
        String geoSrc;
        String houseNumberAdd;
        String name;
        String region;
        String countryISO;
        String geocoderAddress;

        public static LocationView map(Location location) {
            if (location == null) {
                return null;
            }
            return new LocationView(null, location.getCity(), location.getPostalCode(), location.getStreet(), location.getHouseNumber(), null, null, null,
                    false, new BigDecimal(location.getCoordinates().getLongitude()), new BigDecimal(location.getCoordinates().getLatitude()), null, null, null, location.getState(), location.getCountryISO(), null);
        }

        public Location toLocation() {
            Location result = new Location()
                    .setUpdate(false)
                    .setState(state())
                    .setHouseNumber(houseNumber())
                    .setStreet(street)
                    .setCity(city)
                    .setPostalCode(zipcode)
                    .setCoordinates(new GeoLocation()
                            .setLatitude(ofNullable(latitude).map(BigDecimal::toPlainString).orElse(null))
                            .setLongitude(ofNullable(longitude).map(BigDecimal::toPlainString).orElse(null)));
            try {
                result.setCountryISO(countryISO);
            } catch (IllegalArgumentException e) { /* do nothing */ }

            return result;
        }

        private String state() {
            if (!isBlank(region)) {
                return region;
            }
            if (!isBlank(district)) {
                return district;
            }
            return null;
        }

        private String houseNumber() {
            if (isBlank(houseNumberAdd)) {
                return houseNumber;
            }

            return houseNumber + " " + houseNumberAdd;
        }
    }

    @Value
    static class OpeningTimes {
        String dayofweek;
        String ot_from;
        String ot_to;
    }

    @Value
    static class Group {
        Long id;
        String type;
        String name;
        String displayName;
    }

    @Value
    static class PublicInfo {
        String telNumber;
        Boolean nonDiscriminatoryAccess;
        List<OpeningTime> openingTimes = Lists.newArrayList();
    }

    @Value
    static class OpeningTime {
        Long id;
        String dayOfWeek;
        Date from;
        Date to;
    }

    @Value
    static class SimCard {
        Long id;
        String cardId;
        String cardNumber;
        String provider;
        String net;
        String mobile;
        String pin;
        String apnUsername;
        String apnPassword;
        String liDevice;
        String info;
        Boolean ipAddressFix;
    }

    @Value
    static class StationConnectivity {
        Long id;
        String url;
        Boolean urlFixed;
    }

    @Value
    static class CSCState {
        Long id;
        Date approxLastIncoming;
        Date lastTimeout;
        Boolean timeout;
    }

    private StationView() {
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private enum PowerType {
        AC_1_PHASE(1),
        AC_3_PHASE(3),
        DC(1);

        Integer phase;

        public String getPhaseAsString() {
            return phase == null ? null : "" + phase;
        }
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private enum ChargingPointTypeMapping {
        TYPE_1("Type 1 (1phase AC 3,7kW Type2 socket)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("3.7")),
        TYPE_2("Type 2 (1phase AC 3,7kW Type1 attached cable)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_1_PHASE, new BigDecimal("3.7")),
        TYPE_3("Type 3 (1phase AC 3,7kW Type2 attached cable)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_1_PHASE, new BigDecimal("3.7")),
        TYPE_4("Type 4 (1phase AC 7,4kW Type2 socket)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("7.4")),
        TYPE_5("Type 5 (1phase AC 7,4kW Type1 attached cable)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_1_PHASE, new BigDecimal("7.4")),
        TYPE_6("Type 6 (3phase AC 11kW Type2 socket)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        TYPE_7("Type 7 (3phase AC 11kW Type2 attached cable)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_3_PHASE, new BigDecimal("11")),
        TYPE_8("Type 8 (3phase AC 22kW Type2 socket)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        TYPE_9("Type 9 (3phase AC 22kW Type2 attached cable)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_3_PHASE, new BigDecimal("22")),
        TYPE_10("Type 10 (3phase AC 43kW Type2 socket)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("43")),
        TYPE_11("Type 11 (3phase AC 11kW Type2 socket, Schuko)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        TYPE_12("Type 12 (3phase AC 22kW Type2 socket, Schuko)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        DEFAULT_CHARGINGPOINTTYPE("DEFAULT_CHARGINGPOINTTYPE", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        Chademo_11kw("Chademo (DC 11kW 32A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("11")),
        Chademo_18kw("Chademo (DC 18kW 50A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("18")),
        Chademo_22kw("Chademo (DC 18kW 32A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("18")),
        Chademo_45kw("Chademo (DC 45kW 125A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("45")),
        Chademo_50kw("Chademo (DC 50kW 120A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Chademo_50kw_125A("Chademo (DC 50kW 125A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Chademo_50kw_50A("Chademo (DC 50kW 50A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Fast_charger_CHAdeMO("Fast charger CHAdeMO", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        DC_TYPE_1("DC Type 1 (DC 20kW CCS, ChaDemo)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("20")),
        DC_TYPE_2("DC Type 2 (DC 50kW CCS, ChaDemo)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        CS_SMART_SN_22("CS Smart SN22 16A/32A (OCPP)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("22")),
        CS_SMART_SN_22_XML("CS Smart SN22 16A/32A (XML)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("22")),
        CS_SMART_SN_22_MULTI_OCPP("CS Smart SN22 Multiswitch (OCPP)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("22")),
        CS_SMART_SN_22_MULTI_XML("CS Smart SN22 Multiswitch (XML)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("22")),
        KABEL_TYP_2_11Kw("Kabel Typ-2 (AC 11kW 32A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_3_PHASE, new BigDecimal("11")),
        KABEL_TYP_2_3_7Kw("Kabel Typ-2 (AC 3,7kW 16A 230V)", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.AC_1_PHASE, new BigDecimal("3.7")),
        KABEL_CSS_11Kw("Kabel CCS Typ-2 (DC 11kW 32A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("11")),
        KABEL_CSS_18Kw("Kabel CCS Typ-2 (DC 18kW 50A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("18")),
        KABEL_CSS_22Kw("Kabel CCS Typ-2 (DC 22kW 55A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("22")),
        KABEL_CSS_25KW_72A("Kabel CCS Typ-2 (DC 25kW 72A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("25")),
        KABEL_CSS_32KW_80A("Kabel CCS Typ-2 (DC 32kW 80A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("32")),
        KABEL_CSS_45KW_125A("Kabel CCS Typ-2 (DC 45kW 125A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("45")),
        KABEL_CSS_50KW_120A("Kabel CCS Typ-2 (DC 50kW 120A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        KABEL_CSS_50KW_125A("Kabel CCS Typ-2 (DC 50kW 125A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        KABEL_CSS_50KW_50A("Kabel CCS Typ-2 (DC 50kW 50A 400V)", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        KABEL_CHADEMO_22kW("Chademo (DC 22kW 55A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("22")),
        KABEL_CHADEMO_25Kw("Kabel Chademo (DC 25kW 72A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("25")),
        KABEL_CHADEMO_32kW("Chademo (DC 32kW 80A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("32")),
        KABEL_CHADEMO_50kW("Kabel Chademo (DC 50kW 125A 400V)", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        SCHUKO_AC_3KW("Schuko (AC 3kW 16A 230V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("3")),
        SCHUKO_AC_5KW("Schuko (AC 5kW 16A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("5")),
        SOCKET_TYP_2_SCHUKO_11kW_16A("Socket Typ-2 / Schuko (AC 11kW 16A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        SOCKET_TYP_2_SCHUKO_11kW_32A("Socket Typ-2 / Schuko (AC 11kW 32A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        SOCKET_TYP_2_SCHUKO_12kW_32A("Socket Typ-2 / Schuko (AC 12kW 32A 230V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("12")),
        SOCKET_TYP_2_SCHUKO_22kW_32A("Socket Typ-2 / Schuko (AC 22kW 32A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        SOCKET_TYP_2_SCHUKO_22kW_5kw("Socket Typ-2 / Schuko (AC 5kW 16A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("5")),
        SOCKET_TYP_2_11kW_16A("Socket Typ-2 (AC 11kW 16A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        SOCKET_TYP_2_11kW_32A("Socket Typ-2 (AC 11kW 32A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        SOCKET_TYP_2_22kW("Socket Typ-2 (AC 22kW 32A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        SOCKET_TYP_2_22kW_63A("Socket Typ-2 (AC 22kW 63A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        SOCKET_TYP_2_5kw_16A("Socket Typ-2 (AC 5kW 16A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("5")),
        SOCKET_TYP_2_86kW_125A("Socket Typ-2 (AC 86kW 125A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("86")),
        SOCKET_TYP_2_8kW_12A("Socket Typ-2 (AC 8kW 12A 400V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("8")),
        SOCKET_TYP_2_3_7kW_16A("Socket Typ-2 (AC 3,7kW 16A 230V)", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("3.7")),
        SOCKET_3_7kW("3.7kW", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("3.7")),
        Terra_51("Terra 51 Charge Station", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Terra_51_CCS("Terra 51 Charge Station CCS", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Terra_51_CCS_TYPE_2("Terra 51 Charge Station Type 2", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Kwh_11("11kW", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        Kwh_22("22kW", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        PHASE_16A_V3("480V3Phase16A", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        PHASE_32A_V3("480V3Phase32A", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        B1322_1100("B1322-1100", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        Phase16A_240V("m 240V3Phase16A", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("11")),
        Efacec_QC20("Efacec QC20", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, null),
        UNSPECIFIED("Unspecified", null, null, null, null),
        HHLA("HHLA", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("11")),
        MENNEKES_V01("Mennekes v0.1", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("22")),
        MS_01("MS01", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_3_PHASE, new BigDecimal("11")),
        UNKNOWN("UNKNOWN", ConnectorType.TYPE_2_MENNEKES, Format.SOCKET, PowerType.AC_1_PHASE, new BigDecimal("11")),
        FAST_CHARGING_TYP2("Fast Charging Typ2", ConnectorType.TYPE_2_MENNEKES, Format.CABLE, PowerType.DC, new BigDecimal("43")),
        Fast_Charger_CCS("Fast Charger CCS", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("50")),
        Fast_charger_CCS_HP_120kW("Fast charger CCS HP 120kW", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("120")),
        Fast_charger_CCS_HP_175kW("Fast charger CCS HP", ConnectorType.TYPE_2_COMBO, Format.CABLE, PowerType.DC, new BigDecimal("175")),
        CHAdeMO_HP("CHAdeMO HP", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("175")),
        EVTEC_FIX("EVTEC FIX", ConnectorType.CHADEMO, Format.CABLE, PowerType.DC, new BigDecimal("50"));

        String pointType;
        ConnectorType connectorType;
        Format connectorFormat;
        PowerType powerType;
        BigDecimal power;

        private static ChargingPointTypeMapping mappingFor(String pointTypeName, String evseId) {
            Optional<ChargingPointTypeMapping> mapping = Arrays.stream(values())
                    .filter(m -> m.getPointType().equals(pointTypeName))
                    .findFirst();
            return mapping.orElseThrow(() -> new IllegalStateException("" +
                    "No mapping found from charging point type " + pointTypeName +
                    " to any OCPI connector type (at least one is required by OCPI) for charging point with evseId "
                    + evseId
            ));
        }
    }
}
