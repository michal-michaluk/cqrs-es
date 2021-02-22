package devices.configuration.legacy.stationImport;

public class StationImportFailed extends RuntimeException {
    public StationImportFailed(int statusCode) {
        super("Station import failed for page XXXX. Status code: " + statusCode);
    }
}
