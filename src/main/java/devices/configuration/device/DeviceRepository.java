package devices.configuration.device;

import java.util.Optional;

public interface DeviceRepository {
    Optional<Device> get(String deviceId);

    void save(Device device);
}
