package devices.configuration.features.eggplantOutbox;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface EggplantRequestsRepository extends PagingAndSortingRepository<EggplantRequestEntity, Long> {
}
