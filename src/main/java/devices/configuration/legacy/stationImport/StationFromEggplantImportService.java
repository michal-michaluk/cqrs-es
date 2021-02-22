package devices.configuration.legacy.stationImport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.features.Toggles;
import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import devices.configuration.features.toggle.TogglesService;
import devices.configuration.legacy.stationImport.StationsImporter.UpdateScope;
import devices.configuration.legacy.stationImport.report.ImportReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

import static devices.configuration.legacy.stationImport.StationFromEggplantImportService.ImportMode.NORMAL;
import static java.util.Collections.emptyList;

@Service
@Slf4j
public class StationFromEggplantImportService {

    private static final int CIRCUIT_BREAK_LIMIT = 100;
    private static final int PAGE_SIZE = 500;

    private EmobilityOldPlatformClient eggplant;
    private TogglesService togglesService;
    private StationsImporter stationsImporter;

    public StationFromEggplantImportService(EmobilityOldPlatformClient eggplant, TogglesService togglesService, StationsImporter stationsImporter) {
        this.eggplant = eggplant;
        this.togglesService = togglesService;
        this.stationsImporter = stationsImporter;
    }

    public ImportReport importStations(ImportMode mode, Set<UpdateScope> updateScope) {
        ImportReport report = ImportReport.blank();

        if (!toggleEnabled()) {
            return report.setMessage("Toggle disabled");
        }

        int pageNumber = 0;

        Page<StationView.ChargingStation> page;

        do {
            log.info("Importing page " + pageNumber);
            page = getPageOfStations(pageNumber, report);
            stationsImporter.importStations(updateScope, page.getContent(), report);
            pageNumber++;
        } while (page.hasNext() && mode.equals(NORMAL) && pageNumber < CIRCUIT_BREAK_LIMIT);

        log.info(toJson(report));
        return report;
    }

    private Page<StationView.ChargingStation> getPageOfStations(int pageNumber, ImportReport report) {
        try {
            var page = eggplant.getPageOfStations(pageNumber, PAGE_SIZE);
            report.addSucceededPage();
            return page;
        } catch (Exception e) {
            log.error("Error occured - " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
            e.printStackTrace();
            report.addFailedPage(pageNumber, e.getMessage());
            return new PageImpl<>(emptyList(),
                    PageRequest.of(0, 2), 100); // force going to next page
        }
    }

    private boolean toggleEnabled() {
        return togglesService.isEnabled(Toggles.IMPORT_FROM_EGGPLANT, false);
    }

    private String toJson(ImportReport report) {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(report);
        } catch (JsonProcessingException e) {
            log.error("Unable to print report: " + e.getMessage());
            return "";
        }
    }

    public enum ImportMode {TEST, NORMAL}

}
