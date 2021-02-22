package devices.configuration.features.bootNotification;

import devices.configuration.features.Toggles;
import devices.configuration.features.bootNotification.protocol.EggplantProtocolName;
import devices.configuration.features.eggplantOutbox.EggplantService;
import devices.configuration.features.toggle.TogglesService;
import devices.configuration.features.bootNotification.protocol.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;

import static devices.configuration.features.bootNotification.protocol.EggplantProtocolName.OCPP;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import static devices.configuration.features.catalogue.StationException.stationNotFound;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootNotificationService {

    private final StationsCatalogueRepository stationsCatalogueRepository;
    private final TogglesService togglesService;
    private final EggplantService eggplantService;

    public StationProtocolDTO getStationDetails(String stationName) {
        return stationsCatalogueRepository.findByStationName(stationName)
                .map(StationCatalogueEntityToStationProtocolDtoConverter)
                .orElseThrow(() -> stationNotFound(stationName));
    }

    BootNotificationFields getBootNotificationFieldsForStation(String stationName) {
        return stationsCatalogueRepository.findByStationName(stationName)
                .map(BootNotificationFieldsMapper::map)
                .orElseThrow(() -> stationNotFound(stationName));
    }

    @Transactional
    public void handleBootNotificationFromEggplant(String stationName, BootNotificationFields bootNotificationFields) {
        if (togglesService.isEnabled(Toggles.OLD_PLATFORM_BOOT_NOTIFICATION, false)) {

            if(Validator.invalidBootNotificationRequest(stationName, bootNotificationFields)) {
                throw new ResponseStatusException(BAD_REQUEST,
                        format("Eggplant request: %s for station: %s is incorrect", bootNotificationFields, stationName));
            }

            final StationsCatalogueEntity station = stationsCatalogueRepository.findByStationName(stationName)
                    .orElse(new StationsCatalogueEntity(stationName));

            updateSoftwareVersion(station, bootNotificationFields.getSoftwareVersion());
            updateProtocol(station, bootNotificationFields);

            stationsCatalogueRepository.save(station);
        }
    }

    private void updateProtocol(StationsCatalogueEntity station, BootNotificationFields bootNotificationFields) {
        final EggplantProtocolName protocol = EggplantProtocolName.of(bootNotificationFields.getProtocolName());
        if(protocol.isValid()) {
            station.applyNewProtocol(OCPP.getName(), bootNotificationFields.protocolVersion, protocol.getMediaType().getName());
            station.applyNewCsms(protocol.getCsms().getName());
        }
    }

    private void updateSoftwareVersion(StationsCatalogueEntity station, String softwareVersion) {
        if (isNotBlank(softwareVersion)) {
            station.setSoftwareVersion(softwareVersion);
        }
    }

    @Transactional
    public void handleStationConnectedFromCsc(String stationName, StationProtocolFromCsc stationProtocolFromCsc) {
        if (Validator.invalidRequestFromCsc(stationName, stationProtocolFromCsc)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    format("CSC request: %s for station: %s is incorrect", stationProtocolFromCsc, stationName));
        }

        final StationsCatalogueEntity station = stationsCatalogueRepository.findByStationName(stationName)
                .orElse(new StationsCatalogueEntity(stationName));
        final ProtocolDTO protocol = stationProtocolFromCsc.getProtocol();
        station.applyNewProtocol(protocol.getName(), protocol.getVersion(), protocol.getMediaType());
        station.applyNewCsms(stationProtocolFromCsc.getCsms());
        stationsCatalogueRepository.save(station);

        eggplantService.handleStationConnected(stationName, BootNotificationFieldsMapper.map(station));
    }

    private Function<StationsCatalogueEntity, StationProtocolDTO> StationCatalogueEntityToStationProtocolDtoConverter =
            (stationsCatalogueEntity) -> new StationProtocolDTO(
                    new ProtocolDTO(
                            ofNullable(stationsCatalogueEntity.getProtocolName()).map(String::toLowerCase).orElse(null),
                            ofNullable(stationsCatalogueEntity.getProtocolVersion()).map(String::toLowerCase).orElse(null),
                            ofNullable(stationsCatalogueEntity.getMediaType()).map(String::toLowerCase).orElse(null)),
                    stationsCatalogueEntity.getCsms(),
                    stationsCatalogueEntity.getStationName());
}
