package devices.configuration.outbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Repository
interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

    default Page<OutboxMessage> findFirstPage(int batch) {
        PageRequest pageRequest = PageRequest.of(0, batch, Sort.by(new Sort.Order(ASC, "occurrenceTime")));
        return findAll(pageRequest);
    }

    @Transactional(TxType.REQUIRES_NEW)
    default void deleteByIdInSeparateTransaction(UUID id) {
        deleteById(id);
    }

    default void clear() {
        deleteAllInBatch();
    }
}
