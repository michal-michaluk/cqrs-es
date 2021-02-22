package devices.configuration.features.eggplantOutbox;

import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import devices.configuration.features.Toggles;
import devices.configuration.features.bootNotification.BootNotificationFields;
import devices.configuration.features.toggle.TogglesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EggplantOutbox {
    private final EmobilityOldPlatformClient emobilityOldPlatformClient;
    private final EggplantRequestsRepository eggplantRequestsRepository;
    private final TogglesService togglesService;

    @Value("${eggplantOutbox.batch:100}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${eggplantOutbox.delay:PT10S}")
    void handleRequests() {
        if(togglesService.isEnabled(Toggles.SEND_STATION_PROTOCOL_UPDATE_TO_EGGPLANT, false)) {
            getOldestRequests().forEach(this::send);
        }
    }

    private void send(EggplantRequestEntity request) {
        try {
            emobilityOldPlatformClient.sendStationConnected(request.getStationName(), getBootNotificationFields(request));
            eggplantRequestsRepository.deleteById(request.getEntityId());
        } catch (Throwable th) {
            log.warn("Eggplant request: {}, was not send properly", request);
        }
    }

    private BootNotificationFields getBootNotificationFields(EggplantRequestEntity request) {
        return new BootNotificationFields(request.getSoftwareVersion(), request.getProtocolName(), request.getProtocolVersion());
    }

    private List<EggplantRequestEntity> getOldestRequests() {
        final PageRequest pageRequest = PageRequest.of(0, batchSize, Sort.by(Sort.Direction.ASC, "createdAt"));

        return eggplantRequestsRepository.findAll(pageRequest).getContent();
    }
}
