package devices.configuration.features.catalogue.fileImport.eggplant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationsFileImportReport {
    private int allStationsNumber;
    private List<String> importedStations;
    private List<StationImportError> errors;

    @Value
    static class StationImportError {
        private String stationName;
        private String errorMessage;
    }
}

