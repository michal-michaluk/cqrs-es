package devices.configuration.published;

import devices.configuration.features.catalogue.StationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static devices.configuration.features.catalogue.StationException.stationNotFound;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class StationsReadModelController {

    private final StationsRepository repository;

    @GetMapping(value = "/int/stations", params = "version=latest", produces = APPLICATION_JSON_VALUE)
    Page<StationReadModelV1> getStationsLatest(Pageable pageable) {
        return getStationsV1(pageable);
    }

    @GetMapping(value = "/int/stations/{name}", params = "version=latest", produces = APPLICATION_JSON_VALUE)
    StationReadModelV1 getStationByNameLatest(@PathVariable String name) {
        return getStationByNameV1(name);
    }

    @GetMapping(value = "/int/stations", params = "version=1", produces = APPLICATION_JSON_VALUE)
    public Page<StationReadModelV1> getStationsV1(Pageable pageable) {
        return repository.findAll(pageable)
                .map(StationReadModelV1::from);
    }

    @GetMapping(value = "/int/stations/{name}", params = "version=1", produces = APPLICATION_JSON_VALUE)
    public StationReadModelV1 getStationByNameV1(@PathVariable String name) {
        return repository.findByName(name)
                .map(StationReadModelV1::from)
                .orElseThrow(() -> stationNotFound(name));
    }
}
