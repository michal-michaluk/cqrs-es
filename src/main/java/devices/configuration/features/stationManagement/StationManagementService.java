package devices.configuration.features.stationManagement;

import devices.configuration.features.Toggles;
import devices.configuration.features.toggle.TogglesService;
import devices.configuration.outbox.EventOutbox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@AllArgsConstructor
@Slf4j
public class StationManagementService {

    private final EventOutbox eventOutbox;
    private final TogglesService togglesService;

    public void generateStationInstalledToLcp(String stationName, String lcp) {
        if (togglesService.isDisabled(Toggles.CAN_SEND_STATION_INSTALLED_TO_LCP, false)) {
            log.info("StationInstalledToLcp request handling is disabled. I won't send event for: {}", stationName);
            return;
        }

        eventOutbox.store(StationInstalledToLcp.withDefaultValues(stationName, lcp));
        log.info("StationInstalledToLcp for: {} and LCP: {} is stored to send to kafka", stationName, lcp);
    }

    public void generateStationCpoUpdated(String stationName, String cpo) {
        if (togglesService.isDisabled(Toggles.CAN_SEND_STATION_CPO_UPDATED, false)) {
            log.info("StationCpoUpdated request handling is disabled. I won't send event for: {}", stationName);
            return;
        }

        eventOutbox.store(new StationCpoUpdated(cpo));
        log.info("StationCpoUpdated for: {} is stored to send to kafka", stationName);
    }

    public void generateStationCreatedEvent(String stationName) {
        if (togglesService.isDisabled(Toggles.CAN_SEND_STATION_CREATED, false)) {
            log.info("station created request handling is disabled. I won't send event for: {}", stationName);
            throw new EventToggleDisabledException("Station created request handling is disabled.");
        }

        eventOutbox.store(new StationCreated(stationName));
        log.info("station created for: {} is stored to send to kafka", stationName);
    }

    void generateStationDeletedEvent(String stationName) {
        if (togglesService.isDisabled(Toggles.CAN_SEND_STATION_DELETED_EVENT, false)) {
            log.info("station deleted request handling is disabled. I won't send event for: {}", stationName);
            throw new EventToggleDisabledException("Station deleted request handling is disabled.");
        }

        eventOutbox.store(new StationDeleted(stationName));
        log.info("station deleted for: {} is stored to send to kafka", stationName);
    }

    public static class EventToggleDisabledException extends ResponseStatusException {
        public EventToggleDisabledException(String reason) {
            super(FORBIDDEN, reason);
        }
    }
}
