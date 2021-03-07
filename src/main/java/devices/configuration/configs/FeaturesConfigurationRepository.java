package devices.configuration.configs;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeaturesConfigurationRepository
        extends CrudRepository<FeaturesConfigurationEntity, UUID> {
    Optional<FeaturesConfigurationEntity> findFirst1ByNameOrderByTimeDesc(String name);
}

