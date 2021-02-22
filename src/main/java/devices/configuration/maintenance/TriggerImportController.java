package devices.configuration.maintenance;

import devices.configuration.legacy.stationImport.StationFromEggplantImportService;
import devices.configuration.legacy.stationImport.StationFromEggplantImportService.ImportMode;
import devices.configuration.legacy.stationImport.StationsImporter.UpdateScope;
import devices.configuration.legacy.stationImport.report.ImportReport;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
class TriggerImportController {

    private final StationFromEggplantImportService importService;

    @PostMapping("/prv/legacy/import/stations")
    @ResponseStatus(HttpStatus.CREATED)
    public ImportReport importStations(ImportMode mode, String update, String apiKey) {
        ApiKeys.requireOurs(apiKey);
        var updateScope = Arrays.stream(update.split(","))
                .map(String::toUpperCase)
                .map(UpdateScope::valueOf)
                .collect(Collectors.toUnmodifiableSet());
        return importService.importStations(mode, updateScope);
    }
}
