package devices.configuration.device.persistence;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface DeviceEntityRepository extends CrudRepository<DeviceEntity, String> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<DeviceEntity> findByDeviceId(String id);
}

