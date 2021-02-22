package devices.configuration.features.configuration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@AllArgsConstructor
public class FeaturesConfigurationProvider {

    public static final String STATIONS_FILE_IMPORT = "stationsFileImport";
    private FeaturesConfigurationRepository featuresConfigurationRepository;

    public StationImportConfiguration getStationImportConfig() {
        return featuresConfigurationRepository.findByName(STATIONS_FILE_IMPORT)
                .orElseThrow(() -> notFound(STATIONS_FILE_IMPORT))
                .get(StationImportConfiguration.class);
    }

    private ResponseStatusException notFound(String configName) {
        return new ResponseStatusException(NOT_FOUND, "Unable to find configuration in database for config name: " + configName);
    }
}
