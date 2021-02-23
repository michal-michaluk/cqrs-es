package devices.configuration.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Stream;

import static devices.configuration.data.StationException.stationNotFound;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class StationsController {

    private final DeviceRepository repository;
    private final DevicesService service;

    @Transactional(readOnly = true)
    @GetMapping(value = "/pub/stations", params = {"page", "size"}, produces = APPLICATION_JSON_VALUE)
    public Page<StationReadModel.ListItem> getStations(Pageable pageable, String cpo) {
        return repository.findByCpoIn(Set.of(cpo), pageable)
                .map(StationReadModel.ListItem::from);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/pub/stations", produces = APPLICATION_JSON_VALUE)
    public Stream<StationReadModel.ListItem> getStations(@RequestParam(required = false) String search,
                                                         String lcp) {
        return repository.findByLcp(lcp)
                .filter(Query.of(search).stationContainsNameOrEvseIdLike())
                .map(StationReadModel.ListItem::from);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/pub/stations/{name}", produces = APPLICATION_JSON_VALUE)
    public StationReadModel getStationByName(@PathVariable String name) {
        return repository.findByName(name)
                .map(StationReadModel::from)
                .orElseThrow(() -> stationNotFound(name));
    }

    @PatchMapping(value = "/pub/stations/{name}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public StationReadModel patchStation(@PathVariable String name,
                                         @RequestBody @Valid UpdateStation update) {
        return service.updateDevice(name, update.toPreviousForm())
                .map(StationReadModel::from)
                .orElseThrow(() -> stationNotFound(name));
    }
}
