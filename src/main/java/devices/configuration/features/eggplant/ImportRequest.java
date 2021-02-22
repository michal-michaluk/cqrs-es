package devices.configuration.features.eggplant;

import devices.configuration.features.catalogue.fileImport.eggplant.Country;
import lombok.Data;

@Data
public class ImportRequest {
    private final Country country;
    private final String chargingStationTypeName;
    private final String chargingPointTypeName;
    private final String cpoName;
}

