package devices.configuration.features.catalogue.fileImport.csc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationInstallationToCscResponse {
    private int installed = 0;
    private int updated = 0;
    private int failed = 0;

    void updateResult(LineImportResult lineResult) {
        if (lineResult == LineImportResult.INSTALLED) {
            incrementInstalled();
        } else if (lineResult == LineImportResult.UPDATED) {
            incrementUpdated();
        } else {
            incrementFailed();
        }
    }

    private void incrementInstalled() {
        installed++;
    }

    private void incrementUpdated() {
        updated++;
    }

    private void incrementFailed() {
        failed++;
    }

    enum LineImportResult {
        INSTALLED, UPDATED, ERROR
    }
}
