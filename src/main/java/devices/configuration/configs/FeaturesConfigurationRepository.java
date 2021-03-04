package devices.configuration.configs;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FeaturesConfigurationRepository
        extends CrudRepository<FeaturesConfigurationEntity, String> {
    Optional<FeaturesConfigurationEntity> findByName(String name);
}

