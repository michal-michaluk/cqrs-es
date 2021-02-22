package devices.configuration.features.bootNotification;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StationsCatalogueRepository extends CrudRepository<StationsCatalogueEntity, Long> {

    Optional<StationsCatalogueEntity> findByStationName(String stationName);
}
