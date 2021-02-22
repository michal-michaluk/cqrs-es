package devices.configuration.legacy;

import devices.configuration.features.Toggles;
import devices.configuration.features.catalogue.StationUpdate;
import devices.configuration.features.catalogue.StationsService;
import devices.configuration.features.toggle.TogglesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
class LocationSynchronisationService {

    private final StationsService service;
    private final TogglesService toggles;
    private final StationLocationValidator validator;
    private final LocationFixing fixing;

    void syncStationLocation(List<String> stations, StationUpdate update, String payloadAsJson) {
        if (CollectionUtils.isEmpty(stations)) {
            log.warn("Ignoring event {} from eggplant, not provided station names", payloadAsJson);
            return;
        }

        LocationFixing.Status status = fixing.process(update.getLocation())
                .onFailure(throwable -> log.warn("Ignoring event {} from eggplant, as StationUpdate {} has violations: {}",
                        payloadAsJson, update, validator.issues(throwable, update.getLocation())));
        if (status.isFailure()) {
            return;
        }
        if (toggles.isEnabled(Toggles.SYNC_LOCATIONUPDATED_FROM_EGGPLANT, false)) {
            for (String stationName : stations) {
                service.updateStation(stationName, update);
            }
        } else {
            log.info(Toggles.SYNC_LOCATIONUPDATED_FROM_EGGPLANT + " disabled, stations: {}, event: {}, update: {}",
                    stations, payloadAsJson, update);
        }
    }
}
