package devices.configuration.legacy.stationImport;

import devices.configuration.features.catalogue.location.Location;
import devices.configuration.legacy.LocationFixing;
import devices.configuration.legacy.StationLocationValidator;
import devices.configuration.legacy.stationImport.report.ImportReport;
import devices.configuration.features.catalogue.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Slf4j
@Component
@AllArgsConstructor
public class StationsImporter {

    private final StationsRepository stationsRepository;
    private final LocationFixing locationFixing;
    private final StationLocationValidator locationValidator;
    private final StationImageProperties stationImageProperties;

    public enum UpdateScope {LOCATION, CONNECTORS, OWNERSHIP, SETTINGS}

    void importStations(Set<UpdateScope> updateScope, List<StationView.ChargingStation> stations, ImportReport report) {
        stations.stream()
                .map(station -> prepareEntityToSave(updateScope, station, report))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Station::emitUpdated)
                .forEach(stationsRepository::save);
    }

    @Transactional
    public ImportReport syncStation(Set<UpdateScope> updateScope, StationView.ChargingStation imported) {
        ImportReport report = ImportReport.blank();
        prepareEntityToSave(updateScope, imported, report)
                .map(Station::emitUpdated)
                .ifPresent(stationsRepository::save);
        return report;
    }

    private Optional<Station> prepareEntityToSave(Set<UpdateScope> updateScope, StationView.ChargingStation imported, ImportReport report) {
        String stationName = imported.getName();
        Ownership ownership = imported.getOwnership();
        Settings settings = imported.getSettings();
        Location location = location(report, stationName, imported.getLocation());
        List<Connector> connectors = connectors(imported, report, stationName);

        Optional<Station> stationFromDB = stationsRepository.findByName(stationName);

        if (stationFromDB.isEmpty()) {
            report.addCreated();
            return of(new Station()
                    .setName(stationName)
                    .setImageId(stationImageProperties.getId())
                    .setCpo(ownership.getCpo())
                    .setLcp(ownership.getLcp())
                    .setSettings(settings)
                    .setConnectors(connectors)
                    .setLocation(location));
        }
        List<Connector> migrated = connectors.stream()
                .sorted(Comparator.comparing(Connector::getOcppConnectorId))
                .collect(Collectors.toList());
        List<Connector> persisted = stationFromDB.get().getConnectors().stream()
                .sorted(Comparator.comparing(Connector::getOcppConnectorId))
                .collect(Collectors.toList());

        if (Objects.equals(location, stationFromDB.get().getLocation())
                && Objects.equals(migrated, persisted)
                && Objects.equals(ownership, stationFromDB.get().getOwnership())
                && Objects.equals(settings, stationFromDB.get().getSettings())
        ) {
            report.addUnchanged();
            return empty();
        }

        report.addUpdated();
        if (updateScope.contains(UpdateScope.LOCATION)) {
            stationFromDB.get()
                    .setLocation(location);
        }
        if (updateScope.contains(UpdateScope.CONNECTORS)) {
            stationFromDB.get()
                    .setConnectors(connectors);
        }
        if (updateScope.contains(UpdateScope.OWNERSHIP)) {
            stationFromDB.get()
                    .setCpo(ownership.getCpo())
                    .setLcp(ownership.getLcp());
        }
        if (updateScope.contains(UpdateScope.SETTINGS)) {
            stationFromDB.get().setSettings(settings);
        }
        return stationFromDB;
    }

    private List<Connector> connectors(StationView.ChargingStation imported, ImportReport report, String stationName) {
        try {
            return imported.toConnectors();
        } catch (Exception e) {
            log.warn("Impossible to import connectors for {}", stationName, e);
            report.addRejectedConnectors(stationName, e.getMessage());
        }
        return List.of();
    }

    private Location location(ImportReport report, String stationName, StationView.LocationView eggplantLocation) {
        if (eggplantLocation == null) {
            return null;
        }
        Location location = eggplantLocation.toLocation();

        return locationFixing.process(location)
                .onFixed(fix -> report.addFixed(stationName, "fixed location " + eggplantLocation + " with " + fix + " giving " + location))
                .onFailure(throwable -> report.addRejectedLocation(stationName, locationValidator.issues(throwable, location)))
                .isFailure() ? null : location;
    }
}
