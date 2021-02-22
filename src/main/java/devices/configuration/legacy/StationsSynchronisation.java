package devices.configuration.legacy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.features.catalogue.Station;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import devices.configuration.legacy.stationImport.StationView;
import devices.configuration.legacy.stationImport.StationsImporter;
import devices.configuration.legacy.stationImport.report.ImportReport;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
class StationsSynchronisation {

    private final Logger logger;
    private final ObjectMapper mapper;
    private final StationsImporter importer;
    private final EmobilityOldPlatformClient eggplant;
    private final StationsRepository repository;

    @Transactional
    @KafkaListener(topics = "emobility-station-snapshot")
    public void listenOnStationSnapshot(String payloadAsJson) {
        logger.logEvent(payloadAsJson);
        StationSnapshot event;
        String export = null;
        try {
            event = mapper.readValue(payloadAsJson, StationSnapshot.class);
            validate(event);
            if (event.getDeleted()) {
                repository.findByName(event.getStationName())
                        .ifPresentOrElse(station -> {
                            repository.deleteAndEmit(station);
                            logger.logDeleted(station, payloadAsJson);
                        }, () -> logger.logNotInDB(event, payloadAsJson));
            } else {
                export = eggplant.getStationExport(event.getStationName());
                StationView.ChargingStation imported = mapper.readValue(export, StationView.ChargingStation.class);
                ImportReport report = importer.syncStation(Set.of(StationsImporter.UpdateScope.CONNECTORS, StationsImporter.UpdateScope.OWNERSHIP, StationsImporter.UpdateScope.SETTINGS), imported);
                logger.logReport(payloadAsJson, export, report);
            }
        } catch (Exception e) {
            logger.logError(payloadAsJson, export, e);
        }
    }

    private void validate(StationSnapshot stationSnapshot) {
        Objects.requireNonNull(stationSnapshot, "StationSnapshot is null");
        Objects.requireNonNull(stationSnapshot.getStationName(), "StationSnapshot.stationName is required");
        Objects.requireNonNull(stationSnapshot.getDeleted(), "StationSnapshot.deleted is required");
    }


    @Value
    static class StationSnapshot {
        String stationName;
        Boolean deleted;
    }

    @Component
    @AllArgsConstructor
    static class Logger {
        private final ObjectMapper objectMapper;
        private static final org.slf4j.Logger log = LoggerFactory.getLogger("features.legacy.sync");

        void logReport(String payloadAsJson, String export, ImportReport report) {
            if (report.getUpdated() == 1 || report.getCreated() == 1) {
                log.info("StationSnapshot: successful synchronization of station from eggplant {} ", report);
            } else if (report.getUnchanged() == 1) {
                log.debug("StationSnapshot: synchronization of station after event {} not required", payloadAsJson);
            } else {
                log.error("StationSnapshot: synchronization report requires review {}, {} ", export, report);
            }
        }

        void logDeleted(Station station, String payloadAsJson) {
            log.info("StationSnapshot: station {} deleted after event {} ", json(station), payloadAsJson);
        }

        void logNotInDB(StationSnapshot stationSnapshot, String payloadAsJson) {
            log.debug("StationSnapshot: station {} not present in database, ignoring event {} ", stationSnapshot.getStationName(), payloadAsJson);
        }

        void logError(String payloadAsJson, String export, Exception e) {
            log.error("StationSnapshot: issue syncing station from eggplant, after event {}, fetched export {}", payloadAsJson, export, e);
        }

        private String json(Station station) {
            try {
                return objectMapper.writeValueAsString(station);
            } catch (JsonProcessingException e) {
                return "" + station;
            }
        }

        public void logEvent(String payloadAsJson) {
            log.debug("StationSnapshot: got event on topic emobility-station-snapshot: {}", payloadAsJson);
        }
    }
}
