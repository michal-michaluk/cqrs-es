package devices.configuration.features.stationManagement.installation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationInstallationRegistry extends JpaRepository<StationRegistration, String> {
    void deleteAllByStationName(String stationName);
}
