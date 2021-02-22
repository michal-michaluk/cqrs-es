package devices.configuration.features.stationManagement;

import devices.configuration.DomainEvent;
import devices.configuration.legacy.StationLocation;
import devices.configuration.legacy.stationImport.StationView;
import lombok.Builder;
import lombok.Value;

import java.util.List;

import static java.util.Collections.emptyList;

@Value
@Builder
public class StationInstalledToLcp implements DomainEvent {
    String TYPE = "LcpInstallation";
    String lcpName;
    String stationName;
    StationLocation location;
    boolean billing;
    boolean showOnMap;
    boolean autoStart;
    boolean remoteControl;
    boolean reimbursement;
    List<StationView.ChargingPoint> chargingPoints;

    public static StationInstalledToLcp withDefaultValues(String stationName, String lcpName) {
        return builder()
                .stationName(stationName)
                .lcpName(lcpName)
                .autoStart(false)
                .billing(false)
                .reimbursement(false)
                .remoteControl(false)
                .showOnMap(false)
                .location(null)
                .chargingPoints(emptyList())
                .build();
    }
}