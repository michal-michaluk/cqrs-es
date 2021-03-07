package devices.configuration.reads;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceReadModelRepository extends CrudRepository<DeviceReadModelEntity, String> {
    List<DeviceReadModelEntity> findByOperator(String operator);

    List<DeviceReadModelEntity> findByProvider(String provider);
}

