package devices.configuration.data;

import de.vattenfall.emobility.token.EmobilityAuthentication;
import devices.configuration.features.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
    public Page<StationReadModel.ListItem> getStations(Pageable pageable, @ApiIgnore EmobilityAuthentication auth) {
        Set<String> cpo = Authority.requireCpo(auth);
        return repository.findByCpoIn(cpo, pageable)
                .map(StationReadModel.ListItem::from);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/pub/stations", produces = APPLICATION_JSON_VALUE)
    public Stream<StationReadModel.ListItem> getStations(@RequestParam(required = false) String search,
                                                         @ApiIgnore EmobilityAuthentication auth) {
        String lcp = Authority.requireLcp(auth);
        return repository.findByLcp(lcp)
                .filter(Query.of(search).stationContainsNameOrEvseIdLike())
                .map(StationReadModel.ListItem::from);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/pub/stations/{name}", produces = APPLICATION_JSON_VALUE)
    public StationReadModel getStationByName(@PathVariable String name,
                                             @ApiIgnore EmobilityAuthentication auth) {
        Authority authority = Authority.of(auth);
        return repository.findByName(name)
                .filter(station -> authority.matches(station.getOwnership()))
                .map(StationReadModel::from)
                .orElseThrow(() -> stationNotFound(name));
    }

    @Transactional
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
