package devices.configuration.features.catalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationsService {

    private final StationsRepository stationsRepository;
    private final StationFactory stationFactory;

    @Transactional
    public Station updateStation(String stationName, StationUpdate update) {
        Station station = existingOrNew(stationName);
        update
                .onLocationUpdate(station::updateLocation)
                .onOpeningUpdate(station::updateOpening)
                .onSettingsUpdate(station::updateSettings)
                .onOwnershipUpdate(station::updateOwnership);
        stationsRepository.save(station);
        return station;
    }

    private Station existingOrNew(String stationName) {
        return stationsRepository
                .findByName(stationName)
                .orElseGet(() -> {
                    log.info("Didn't find station with name " + stationName + ". Saving a new one.");
                    return stationFactory.newStation(stationName);
                });
    }

    Page<Station> findAll(Specification<Station> specification, Pageable pageable) {
        return stationsRepository.findAll(Specification.where(specification), pageable);
    }

    List<Station> findAll() {
        return (List<Station>) stationsRepository.findAll();
    }

    public Optional<Station> findByName(String stationName) {
        return stationsRepository.findByName(stationName);
    }
}
