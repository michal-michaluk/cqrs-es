package devices.configuration.legacy.stationImport;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GeoLocationView {
    private String latitude;
    private String longitude;
}
