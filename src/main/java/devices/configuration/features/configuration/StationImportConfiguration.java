package devices.configuration.features.configuration;

import lombok.Value;

import java.util.List;

@Value
public class StationImportConfiguration implements FeatureConfiguration {
    private List<String> cpos;
    private List<ChargingStationType> stationTypes;
    private List<String> chargingPointTypes;
}

@Value
class ChargingStationType {
    private String manufacturer;
    private String name;
}