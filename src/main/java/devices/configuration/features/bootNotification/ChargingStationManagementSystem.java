package devices.configuration.features.bootNotification;

import lombok.Getter;

public enum ChargingStationManagementSystem {

    OLD_PLATFORM("emobility"),
    CHARGING_STATION_COMMUNICATION("charging-station-communication"),
    INVALID("");

    @Getter
    String name;

    ChargingStationManagementSystem(String name) {
        this.name = name;
    }

    public static ChargingStationManagementSystem of(String value) {
        for (ChargingStationManagementSystem csms: ChargingStationManagementSystem.values()) {
            if (csms.name.equals(value.toLowerCase())) {
                return csms;
            }
        }

        return INVALID;
    }

    public boolean isValid() {
        return !INVALID.equals(this);
    }
}
