package devices.configuration.features.stationManagement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class StationManagementController {

    private final StationManagementService stationManagementService;

    @PutMapping("/station/{stationName}/created")
    public void sendStationCreated(@PathVariable String stationName) {
        log.info("got station created request for: {}", stationName);
        stationManagementService.generateStationCreatedEvent(stationName);
    }

    @PutMapping("/station/{stationName}/deleted")
    public void sendStationDeleted(@PathVariable String stationName) {
        log.info("got station deleted request for: {}", stationName);
        stationManagementService.generateStationDeletedEvent(stationName);
    }
}
