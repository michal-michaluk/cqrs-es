package devices.configuration.features.eggplantOutbox;

import devices.configuration.features.bootNotification.BootNotificationFields;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
@Slf4j
@AllArgsConstructor
public class EggplantService {
    private final EggplantRequestsRepository eggplantRequestsRepository;
    private final Clock clock;

    public void handleStationConnected(String stationName, BootNotificationFields bootNotificationFields) {
        EggplantRequestEntity request = EggplantRequestEntity.of(stationName, bootNotificationFields, Instant.now(clock));
        request.setSoftwareVersion("");
        eggplantRequestsRepository.save(request);
    }
}
