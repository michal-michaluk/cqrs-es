package devices.configuration.device.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceEventRepository extends CrudRepository<DeviceEventEntity, UUID> {
    @Query(value = "select distinct on (type) *" +
            " from device_events" +
            " where deviceId = :deviceId" +
            " order by type, time desc", nativeQuery = true)
    List<DeviceEventEntity> findLastEvents(String deviceId);
}

