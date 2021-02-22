package devices.configuration.features.images;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ImagesRepository extends JpaRepository<ImageEntity, String> {

    Optional<ImageEntity> findByIdAndTombstoneIsNull(String id);

    List<ImageEntityIdProjection> findAllProjectedByAndTombstoneIsNull();

}
