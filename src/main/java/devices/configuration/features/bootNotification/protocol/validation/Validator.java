package devices.configuration.features.bootNotification.protocol.validation;

import devices.configuration.features.bootNotification.BootNotificationFields;
import devices.configuration.features.bootNotification.StationProtocolFromCsc;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
public class Validator {
    private static ProtocolInformationValidator cscRequestValidators =
            ProtocolInformationValidator.blankStationName
                    .and(ProtocolInformationValidator.notCscCsms)
                    .and(ProtocolInformationValidator.incorrectCsms)
                    .and(ProtocolInformationValidator.protocolFieldsAreNotProvided)
                    .and(ProtocolInformationValidator.mediaTypeNotSupportedByCsc)
                    .and(ProtocolInformationValidator.protocolNotSupportedByCsc);

    public static boolean invalidRequestFromCsc(String stationName, StationProtocolFromCsc stationProtocolFromCsc) {
        if(stationProtocolFromCsc == null) {
            log.warn("For given station: {}, the StationProtocolFromCsc request is null", stationName);
            return true;
        }

        final ProtocolInformation protocolInformation = new ProtocolInformation(stationName,
                stationProtocolFromCsc.getProtocol().getName(), stationProtocolFromCsc.getProtocol().getVersion(),
                stationProtocolFromCsc.getCsms(), stationProtocolFromCsc.getProtocol().getMediaType());

        return isInvalid(cscRequestValidators, stationName, stationProtocolFromCsc.toString(), protocolInformation);
    }

    static ProtocolInformationValidator eggplantOrCscRequestValidator =
            ProtocolInformationValidator.blankStationName
                    .and(ProtocolInformationValidator.onlyOneProtocolFieldIsProvided)
                    .and(ProtocolInformationValidator.protocolNotSupportedByEggplant);

    public static boolean invalidBootNotificationRequest(String stationName, BootNotificationFields bootNotificationFields) {
        final ProtocolInformation protocolInformation = new ProtocolInformation(stationName,
                bootNotificationFields.getProtocolName(),
                bootNotificationFields.getProtocolVersion(),
                null,
                null);

        return isInvalid(eggplantOrCscRequestValidator, stationName, bootNotificationFields.toString(), protocolInformation);
    }

    private static boolean isInvalid(ProtocolInformationValidator requestValidator, String stationName, String request,
                                     ProtocolInformation protocolInformation) {
        final Set<ProtocolInformationValidationMessage> validationResult = requestValidator.apply(protocolInformation);
        if(!validationResult.isEmpty()) {
            System.out.println(format("For given station:%S, and request:%S, the following errors were found: %S", stationName, request,
                    validationResult.stream().map(Objects::toString).collect(Collectors.joining(","))));
            log.warn("For given station:{}, and request: {}, the following errors were found: {}", stationName, request,
                    validationResult.stream().map(Objects::toString).collect(Collectors.joining(",")));
            return true;
        }
        return false;
    }
}
