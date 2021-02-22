package devices.configuration.features.catalogue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface StationsRepository extends PagingAndSortingRepository<Station, UUID>, JpaSpecificationExecutor<Station> {
    Optional<Station> findByName(String name);

    Stream<Station> findByLcp(String lcp);

    Page<Station> findByCpoIn(Set<String> cpo, Pageable pageable);

    default void deleteAndEmit(Station station) {
        save(station.emitDeleted());
        delete(station);
    }

}
