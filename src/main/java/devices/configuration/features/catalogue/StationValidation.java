package devices.configuration.features.catalogue;

import io.vavr.collection.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Value
public class StationValidation {

    boolean validForCharging;
    boolean validForPublication;
    Violations violations;

    static StationValidation validate(Station station) {
        Violations.ViolationsBuilder violations = Violations.violationsOf(station);
        boolean validForCharging = isValidForCharging(violations);
        boolean validForPublication = isValidForPublication(violations);
        return new StationValidation(
                validForCharging,
                validForPublication,
                violations
                        .publicAccessButInvalidForCharging(station.getSettings().isPublicAccess() && !validForCharging)
                        .publicAccessButInvalidForPublication(station.getSettings().isPublicAccess() && !validForPublication)
                        .build()
        );
    }

    @Value
    @Builder(access = AccessLevel.PACKAGE)
    static class Violations {
        Boolean missingCpo;
        Boolean missingLcp;
        Boolean missingLocation;
        Boolean showOnMapButMissingLocation;
        Boolean showOnMapButNoPublicAccess;
        Boolean publicAccessButInvalidForPublication;
        Boolean publicAccessButInvalidForCharging;
        Boolean noConnectors;
        Boolean notConsecutiveOcppConnectorId;
        List<ConnectorViolations> connectors;

        private static Violations.ViolationsBuilder violationsOf(Station station) {
            return Violations.builder()
                    .missingCpo(station.getCpo() == null)
                    .missingLcp(station.getLcp() == null)
                    .missingLocation(station.getLocation() == null)
                    .showOnMapButMissingLocation(station.getSettings().isShowOnMap() && station.getLocation() == null)
                    .showOnMapButNoPublicAccess(station.getSettings().isShowOnMap() && !station.getSettings().isPublicAccess())
                    .noConnectors(station.getConnectors().isEmpty())
                    .notConsecutiveOcppConnectorId(!station.getConnectors().stream()
                            .map(Connector::getOcppConnectorId)
                            .map(Objects::toString)
                            .collect(Collectors.joining(", "))
                            .equals(Stream.rangeClosed(1, station.getConnectors().size())
                                    .map(Objects::toString)
                                    .collect(Collectors.joining(", ")))
                    )
                    .connectors(station.getConnectors().stream().map(c -> ConnectorViolations.builder()
                            .missingEvseId(StringUtils.isBlank(c.getEvseId()))
                            .missingPointName(StringUtils.isBlank(c.getName()))
                            .wrongOcppConnectorId(c.getOcppConnectorId() < 0 || c.getOcppConnectorId() > station.getConnectors().size())
                            .missingAmperage(c.getAmps() == null)
                            .missingVoltage(c.getVoltage() == null)
                            .missingType(c.getType() == null)
                            .missingFormat(c.getFormat() == null)
                            .sameValueForACAndDC(c.isAc() == c.isDc())
                            .missingACPhase(c.isAc() && c.getPhases() == null)
                            .providedDCPhase(c.isDc() && c.getPhases() != null)
                            .acChargerWithDcSocket(c.isAc() && Set.of(ConnectorType.CHADEMO, ConnectorType.TYPE_2_COMBO).contains(c.getType()))
                            .dcChargerWithAcSocket(c.isDc() && Set.of(ConnectorType.TYPE_1_SAE_J1772, ConnectorType.TYPE_2_MENNEKES).contains(c.getType()))
                            .build()).collect(Collectors.toUnmodifiableList()));
        }
    }

    private static boolean isValidForCharging(Violations.ViolationsBuilder violations) {
        return !violations.missingCpo &&
                !violations.noConnectors &&
                !violations.notConsecutiveOcppConnectorId &&
                violations.connectors.stream().allMatch(ConnectorViolations::isValidForCharging);
    }

    private static boolean isValidForPublication(Violations.ViolationsBuilder violations) {
        return !violations.showOnMapButMissingLocation &&
                !violations.noConnectors &&
                violations.connectors.stream().allMatch(ConnectorViolations::isValidForPublication);
    }

    @Value
    @Builder(access = AccessLevel.PACKAGE)
    public static class ConnectorViolations {
        Boolean missingEvseId;
        Boolean missingPointName;
        Boolean wrongOcppConnectorId;
        Boolean missingAmperage;
        Boolean missingVoltage;
        Boolean missingType;
        Boolean missingFormat;
        Boolean sameValueForACAndDC;
        Boolean missingACPhase;
        Boolean providedDCPhase;
        Boolean dcChargerWithAcSocket;
        Boolean acChargerWithDcSocket;

        public boolean isValidForCharging() {
            return !missingEvseId &&
                    !missingPointName &&
                    !wrongOcppConnectorId;
        }

        public boolean isValidForPublication() {
            return !missingEvseId &&
                    !missingAmperage &&
                    !missingVoltage &&
                    !missingType &&
                    !missingFormat &&
                    !sameValueForACAndDC &&
                    !missingACPhase &&
                    !providedDCPhase &&
                    !dcChargerWithAcSocket &&
                    !acChargerWithDcSocket;
        }
    }
}
