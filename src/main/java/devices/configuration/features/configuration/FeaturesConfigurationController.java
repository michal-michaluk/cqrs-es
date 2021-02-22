package devices.configuration.features.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class FeaturesConfigurationController {

    private final FeaturesConfigurationProvider configurationProvider;

    @GetMapping(value = "/installation/configuration")
    @ResponseBody
    public StationImportConfiguration getImportConfiguration() {
        return configurationProvider.getStationImportConfig();
    }
}
