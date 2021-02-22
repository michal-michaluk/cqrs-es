package devices.configuration.features.bootNotification.protocol.validation;

import devices.configuration.features.bootNotification.ChargingStationManagementSystem;
import devices.configuration.features.bootNotification.protocol.CscSupportedProtocol;
import devices.configuration.features.bootNotification.protocol.EggplantSupportedProtocol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static devices.configuration.features.bootNotification.ChargingStationManagementSystem.CHARGING_STATION_COMMUNICATION;
import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.JSON;
import static java.util.Collections.emptySet;
import static java.util.function.Predicate.not;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface ProtocolInformationValidator extends Function<ProtocolInformation, Set<ProtocolInformationValidationMessage>> {
    ProtocolInformationValidator blankStationName = rule(Predicates.stationNameIsBlank,
            ProtocolInformationValidationMessage.STATION_NAME_IS_BLANK);

    ProtocolInformationValidator incorrectCsms = rule(Predicates.csmsIsIncorrect,
            ProtocolInformationValidationMessage.CSMS_IS_INCORRECT);

    ProtocolInformationValidator notCscCsms = rule(not(Predicates.isCsc),
            ProtocolInformationValidationMessage.NOT_CSC_CSMS);

    ProtocolInformationValidator onlyOneProtocolFieldIsProvided =
            rule(Predicates.onlyOneProtocolFieldIsProvided,
                    ProtocolInformationValidationMessage.ONLY_ONE_PROTOCOL_FIELD_IS_PROVIDED);

    ProtocolInformationValidator protocolFieldsAreNotProvided =
            rule(not(Predicates.protocolFieldsAreProvided),
                    ProtocolInformationValidationMessage.PROTOCOL_FIELDS_ARE_NOT_PROVIDED);

    ProtocolInformationValidator protocolNotSupportedByEggplant =
            rule(Predicates.protocolNotSupportedByEggplant,
                    ProtocolInformationValidationMessage.PROTOCOL_NOT_SUPPORTED_BY_EGGPLANT);

    ProtocolInformationValidator protocolNotSupportedByCsc =
            rule(Predicates.protocolNotSupportedByCsc,
                    ProtocolInformationValidationMessage.PROTOCOL_NOT_SUPPORTED_BY_CSC);

    ProtocolInformationValidator mediaTypeNotSupportedByCsc =
            rule(Predicates.mediaTypeNotSupportedByCsc,
                    ProtocolInformationValidationMessage.MEDIA_TYPE_NOT_SUPPORTED_BY_CSC);

    static ProtocolInformationValidator rule(final Predicate<ProtocolInformation> predicate,
                                             final ProtocolInformationValidationMessage rule) {
        return obj -> predicate.test(obj) ? Collections.singleton(rule) : emptySet();
    }

    default ProtocolInformationValidator and(ProtocolInformationValidator other) {
        return protocolInformation -> merge(this.apply(protocolInformation), other.apply(protocolInformation));
    }

    static <T> Set<T> merge (Set<T> first, Set<T> second) {
        final HashSet<T> merged = new HashSet<>(first);
        merged.addAll(second);
        return merged;
    }

    interface Predicates extends Predicate<ProtocolInformation> {
        Predicates stationNameIsBlank =
                protocolInformation -> isBlank(protocolInformation.stationName);

        Predicates csmsIsIncorrect =
                protocolInformation -> isBlank(protocolInformation.csms) ||
                        !ChargingStationManagementSystem.of(protocolInformation.csms).isValid();

        Predicates protocolNameIsBlank =
                protocolInformation -> isBlank(protocolInformation.protocolName);

        Predicates protocolVersionIsBlank =
                protocolInformation -> isBlank(protocolInformation.protocolVersion);

        Predicates onlyOneProtocolFieldIsProvided =
                protocolInformation ->
                        protocolNameIsBlank.and(protocolVersionIsBlank.negate()).or(
                                protocolNameIsBlank.negate().and(protocolVersionIsBlank)
                        ).test(protocolInformation);

        Predicates protocolFieldsAreProvided =
                protocolInformation -> !protocolNameIsBlank.test(protocolInformation) &&
                        !protocolVersionIsBlank.test(protocolInformation);

        Predicates protocolNotSupportedByEggplant =
                protocolInformation -> protocolFieldsAreProvided.test(protocolInformation) &&
                        !EggplantSupportedProtocol.of(protocolInformation.protocolName, protocolInformation.protocolVersion).isValid();

        Predicates protocolNotSupportedByCsc =
                protocolInformation ->
                        !CscSupportedProtocol.of(protocolInformation.protocolName, protocolInformation.protocolVersion).isValid();

        Predicates mediaTypeNotSupportedByCsc =
                protocolInformation -> !protocolInformation.mediaType.equalsIgnoreCase(JSON.getName());

        Predicates isCsc =
                protocolInformation -> CHARGING_STATION_COMMUNICATION.getName().equalsIgnoreCase(protocolInformation.csms);
    }
}
