package devices.configuration.data;

import devices.configuration.data.location.GeoLocation;
import devices.configuration.data.location.Location;
import devices.configuration.data.location.OpeningHours;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static devices.configuration.data.location.OpeningHours.OpeningTime.closed;
import static devices.configuration.data.location.OpeningHours.OpeningTime.opened;

public class StationsFixture {

    public static Device evb() {
        return evb("EVB-" + UUID.randomUUID());
    }

    public static Device evb(String stationName) {
        return new Device()
                .setName(stationName)
                .setPhysicalReference("EVB-123234")
                .setMaxNumberOfOutlets(20)
                .setVendor("EVBOX")
                .setProduct("ELVI")
                .setProductDetails("ELVI273")
                .setColor("Gray")
                .setComment1("looks like a shoebox")
                .setComment2("just some free fields plhed")
                .setCapabilities(new StationCapabilities()
                        .setAvailableOcppVersions("1.5/1.6")
                        .setDebitPayment(false)
                        .setReservation(true)
                        .setCreditPayment(false)
                        .setRfidReader(true)
                        .setRemoteStart(true)
                        .setScnDlb(true)
                        .setTokenGrouping(true)
                        .setSmartCharging(true)
                        .setUnlock(true)
                        .setAc(true)
                        .setDc(false))
                .setConnectors(List.of(
                        new Connector()
                                .setName("EVB-123234_C1")
                                .setPhysicalReference("AB3459")
                                .setEvseId("EVB-123234")
                                .setType(ConnectorType.TYPE_2_MENNEKES)
                                .setFormat(Format.SOCKET)
                                .setVoltage("230")
                                .setPhases("3")
                                .setAmps("32")
                                .setAc(true)
                                .setDc(false),
                        new Connector()
                                .setName("EVB-123234_C2")
                                .setPhysicalReference("AB3459")
                                .setEvseId("EVB-123234")
                                .setType(ConnectorType.CHADEMO)
                                .setFormat(Format.CABLE)
                                .setVoltage("230")
                                .setPhases(null)
                                .setAmps("32")
                                .setAc(false)
                                .setDc(true)
                ))
                .setImageId("placeholder")
                .setLocation(Locations.rooseveltlaanInGent())
                .setOpeningHours(Opening.alwaysOpen())
                .setSettings(Settings.defaultSettings());
    }

    public static Device matchinEggplantStationsExport() {
        // instance matching classpath ressurce ./eggplant-stations-export.json
        return new Device()
                .setName("015021")
                .setCpo("Vattenfall DE")
                .setLcp("LCP vattenfall-de")
                .setImageId("fake-image")
                .setSettings(Settings.builder()
                        .billing(true)
                        .reimbursement(true)
                        .publicAccess(true)
                        .showOnMap(true)
                        .autoStart(false)
                        .remoteControl(true)
                        .build())
                .setLocation(new Location()
                        .setCountryISO("DEU")
                        .setCity("Berlin")
                        .setPostalCode("10439")
                        .setStreet("Kanzowstrasse")
                        .setHouseNumber("1")
                        .setCoordinates(new GeoLocation()
                                .setLongitude("13.42681")
                                .setLatitude("52.54529"))
                )
                .setConnectors(List.of(
                        new Connector()
                                .setName("015021_R")
                                .setEvseId("DE*VAT*E*ME015021*R")
                                .setPhysicalReference(null)
                                .setOcppConnectorId(1)
                                .setType(ConnectorType.TYPE_2_MENNEKES)
                                .setFormat(Format.SOCKET)
                                .setPower(new BigDecimal("11040.0"))
                                .setVoltage("230.0")
                                .setAmps("16.0")
                                .setPhases("3")
                                .setAc(true)
                                .setDc(false),
                        new Connector()
                                .setName("015021_L")
                                .setEvseId("DE*VAT*E*ME015021*L")
                                .setPhysicalReference(null)
                                .setOcppConnectorId(2)
                                .setType(ConnectorType.TYPE_2_MENNEKES)
                                .setFormat(Format.SOCKET)
                                .setPower(new BigDecimal("11040.0"))
                                .setVoltage("230.0")
                                .setAmps("16.0")
                                .setPhases("3")
                                .setAc(true)
                                .setDc(false)
                ));
    }

    public static Device matchinPactExampleEFACECQC0032() {
        return new Device()
                .setName("EFACECQC0032")
                .setImageId("-")
                .setPhysicalReference(null)
                .setCpo("InCharge SE")
                .setLcp("LCP anneli.rosenius")
                .setLocation(new Location()
                        .setStreet("OK Norrbotten - Robertsviksgatan")
                        .setHouseNumber("3")
                        .setCity("Luleå")
                        .setPostalCode("97241")
                        .setState(null)
                        .setCountryISO("SWE")
                        .setCoordinates(new GeoLocation()
                                .setLongitude("22.148478")
                                .setLatitude("65.590671")
                        )
                )
                .setConnectors(List.of(
                        new Connector()
                                .setName("EFACECQC0032_1")
                                .setOcppConnectorId(1)
                                .setPhysicalReference(null)
                                .setEvseId("SE*VAT*E*2457")
                                .setType(ConnectorType.CHADEMO)
                                .setFormat(Format.CABLE)
                                .setDc(true)
                                .setAc(false)
                                .setPower(new BigDecimal("6400.00"))
                                .setVoltage("80.00")
                                .setAmps("80.00")
                ))
                .setSettings(Settings.builder()
                        .autoStart(false)
                        .remoteControl(true)
                        .billing(true)
                        .reimbursement(true)
                        .showOnMap(true)
                        .publicAccess(true)
                        .build());
    }

    public static class Locations {
        public static Location rooseveltlaanInGent() {
            return new Location()
                    .setStreet("F.Rooseveltlaan")
                    .setHouseNumber("3A")
                    .setCity("Gent")
                    .setPostalCode("9000")
                    .setCountryISO("BEL")
                    .setCoordinates(new GeoLocation()
                            .setLatitude("51.047599")
                            .setLongitude("3.729944")
                    );
        }

        public static Location dusartstraatInAmsterdam() {
            return new Location()
                    .setStreet("Dusartstraat")
                    .setHouseNumber("3")
                    .setCity("Amsterdam")
                    .setPostalCode("1072HS")
                    .setCountryISO("NLD")
                    .setCoordinates(new GeoLocation()
                            .setLatitude("52.352206")
                            .setLongitude("4.809561")
                    );
        }

        public static Location chujtenInChina() {
            return new Location()
                    .setStreet("Altay")
                    .setHouseNumber("13")
                    .setCity("Chüjten")
                    .setPostalCode("XXXXX")
                    .setCountryISO("CHN")
                    .setCoordinates(new GeoLocation()
                            .setLatitude("49.150702")
                            .setLongitude("87.820474")
                    );
        }
    }

    public static class Opening {
        public static OpeningHours alwaysOpen() {
            return OpeningHours.alwaysOpen();
        }

        public static OpeningHours openAtWorkWeek() {
            return OpeningHours.openAt(
                    opened(8, 17),
                    opened(8, 17),
                    opened(8, 17),
                    opened(8, 17),
                    opened(8, 17),
                    closed(),
                    closed()
            );
        }
    }
}