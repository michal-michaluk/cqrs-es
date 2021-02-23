package devices.configuration.data;

import devices.configuration.JsonAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

class StationValidationTest {

    @Test
    void should_find_no_violations() {
        JsonAssert.assertThat(StationValidation.validate(StationsFixture.matchinEggplantStationsExport()))
                .isExactlyLike(new StationValidation(true, true,
                        StationValidation.Violations.builder()
                                .missingCpo(false)
                                .missingLcp(false)
                                .missingLocation(false)
                                .showOnMapButMissingLocation(false)
                                .showOnMapButNoPublicAccess(false)
                                .publicAccessButInvalidForPublication(false)
                                .publicAccessButInvalidForCharging(false)
                                .noConnectors(false)
                                .notConsecutiveOcppConnectorId(false)
                                .connectors(List.of(
                                        StationValidation.ConnectorViolations.builder()
                                                .missingEvseId(false)
                                                .missingPointName(false)
                                                .wrongOcppConnectorId(false)
                                                .missingAmperage(false)
                                                .missingVoltage(false)
                                                .missingType(false)
                                                .missingFormat(false)
                                                .sameValueForACAndDC(false)
                                                .missingACPhase(false)
                                                .providedDCPhase(false)
                                                .acChargerWithDcSocket(false)
                                                .dcChargerWithAcSocket(false)
                                                .build(),
                                        StationValidation.ConnectorViolations.builder()
                                                .missingEvseId(false)
                                                .missingPointName(false)
                                                .wrongOcppConnectorId(false)
                                                .missingAmperage(false)
                                                .missingVoltage(false)
                                                .missingType(false)
                                                .missingFormat(false)
                                                .sameValueForACAndDC(false)
                                                .missingACPhase(false)
                                                .providedDCPhase(false)
                                                .acChargerWithDcSocket(false)
                                                .dcChargerWithAcSocket(false)
                                                .build()
                                )).build())
                );
    }

    @Test
    void should_find_missing_eveseId() {
        JsonAssert.assertThat(StationValidation.validate(givenStationWithoutEvseId()))
                .isExactlyLike(new StationValidation(false, false,
                        StationValidation.Violations.builder()
                                .missingCpo(false)
                                .missingLcp(false)
                                .missingLocation(false)
                                .showOnMapButMissingLocation(false)
                                .showOnMapButNoPublicAccess(false)
                                .publicAccessButInvalidForPublication(true)
                                .publicAccessButInvalidForCharging(true)
                                .noConnectors(false)
                                .notConsecutiveOcppConnectorId(false)
                                .connectors(List.of(
                                        StationValidation.ConnectorViolations.builder()
                                                .missingEvseId(true)
                                                .missingPointName(false)
                                                .wrongOcppConnectorId(false)
                                                .missingAmperage(false)
                                                .missingVoltage(false)
                                                .missingType(false)
                                                .missingFormat(false)
                                                .sameValueForACAndDC(false)
                                                .missingACPhase(false)
                                                .providedDCPhase(false)
                                                .acChargerWithDcSocket(false)
                                                .dcChargerWithAcSocket(false)
                                                .build(),
                                        StationValidation.ConnectorViolations.builder()
                                                .missingEvseId(false)
                                                .missingPointName(false)
                                                .wrongOcppConnectorId(false)
                                                .missingAmperage(false)
                                                .missingVoltage(false)
                                                .missingType(false)
                                                .missingFormat(false)
                                                .sameValueForACAndDC(false)
                                                .missingACPhase(false)
                                                .providedDCPhase(false)
                                                .acChargerWithDcSocket(false)
                                                .dcChargerWithAcSocket(false)
                                                .build()
                                )).build())
                );

    }

    @Test
    void should_find_connector_issues() {
        JsonAssert.assertThat(StationValidation.validate(StationsFixture.evb()))
                .isExactlyLike(new StationValidation(false, true,
                        StationValidation.Violations.builder()
                                .missingCpo(true)
                                .missingLcp(true)
                                .missingLocation(false)
                                .showOnMapButMissingLocation(false)
                                .showOnMapButNoPublicAccess(false)
                                .publicAccessButInvalidForPublication(false)
                                .publicAccessButInvalidForCharging(false)
                                .noConnectors(false)
                                .notConsecutiveOcppConnectorId(true)
                                .connectors(List.of(
                                        StationValidation.ConnectorViolations.builder()
                                                .missingEvseId(false)
                                                .missingPointName(false)
                                                .wrongOcppConnectorId(true)
                                                .missingAmperage(false)
                                                .missingVoltage(false)
                                                .missingType(false)
                                                .missingFormat(false)
                                                .sameValueForACAndDC(false)
                                                .missingACPhase(false)
                                                .providedDCPhase(false)
                                                .acChargerWithDcSocket(false)
                                                .dcChargerWithAcSocket(false)
                                                .build(),
                                        StationValidation.ConnectorViolations.builder()
                                                .missingEvseId(false)
                                                .missingPointName(false)
                                                .wrongOcppConnectorId(true)
                                                .missingAmperage(false)
                                                .missingVoltage(false)
                                                .missingType(false)
                                                .missingFormat(false)
                                                .sameValueForACAndDC(false)
                                                .missingACPhase(false)
                                                .providedDCPhase(false)
                                                .acChargerWithDcSocket(false)
                                                .dcChargerWithAcSocket(false)
                                                .build()
                                )).build())
                );
    }

    private Device givenStationWithoutEvseId() {
        Device station = StationsFixture.matchinEggplantStationsExport();
        station.getConnectors().get(0).setEvseId(null);
        station.setSettings(station.getSettings().toBuilder().publicAccess(true).build());
        return station;
    }
}
