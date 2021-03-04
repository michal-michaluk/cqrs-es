package devices.configuration.device;

import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Optional;

public interface DeviceRepository {
    Page<Device> findAll(Pageable pageable);

    Optional<Device> findByDeviceId(String deviceId);

    void save(Device device);
}
