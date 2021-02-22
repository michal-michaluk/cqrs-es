package devices.configuration.features.communication.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface MessageJpaRepository extends JpaRepository<StationMessage, UUID> {
    Optional<StationMessage> findFirst1ByStationAndTypeOrderByTimeDesc(String station, String type);
}
