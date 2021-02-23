package devices.configuration.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface DeviceRepository extends PagingAndSortingRepository<Device, UUID>, JpaSpecificationExecutor<Device> {
    Optional<Device> findByName(String name);

    Stream<Device> findByLcp(String lcp);

    Page<Device> findByCpoIn(Set<String> cpo, Pageable pageable);

}
