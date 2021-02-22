package devices.configuration.features.catalogue;

import devices.configuration.features.Authority;
import de.vattenfall.emobility.token.EmobilityAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
class StationsCatalogueRestController {

    private final StationsService stationsService;
    private final StationsRepository repository;

    @GetMapping(value = "/pub/stations/", params = "projection=forehand")
    @Transactional
    public Stream<ForehandStationSummary> getStations(
            @RequestParam(required = false) String search,
            @ApiIgnore EmobilityAuthentication auth) {
        String lcp = Authority.requireLcp(auth);
        return repository.findByLcp(lcp)
                .filter(Query.of(search).stationContainsNameOrEvseIdLike())
                .map(ForehandStationSummary::from);
    }

    @GetMapping(value = "/pub/stations", params = {"page", "size", "projection=forehand"}, produces = APPLICATION_JSON_VALUE)
    @Transactional
    public Slice<ForehandStationSummary> getChargingStations(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) List<String> statuses,
            Pageable pageable,
            @ApiIgnore EmobilityAuthentication auth) {
        String lcp = Authority.requireLcp(auth);
        return sliceFor(repository.findByLcp(lcp)
                .filter(Query.of(search).stationContainsNameOrEvseIdLike())
                .sorted(Comparator.comparing(Station::getName, String.CASE_INSENSITIVE_ORDER))
                .skip(pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .map(ForehandStationSummary::from)
                .collect(Collectors.toList()), pageable);
    }

    @GetMapping(value = "/installation/stations", produces = APPLICATION_JSON_VALUE)
    public Page<Station> getStations(@RequestParam(value = "name", required = false) String name, Pageable pageable) {
        return stationsService.findAll(new StationSpecification().withNameContaining(name), pageable);
    }

    @GetMapping(value = "/installation/stations", produces = "text/csv")
    public void getStations(HttpServletResponse response) throws IOException {
        List<Station> stations = stationsService.findAll();
        CsvWriter.writeStations(response.getWriter(), stations);
    }

    @GetMapping(value = "/installation/stations/{stationName}", produces = APPLICATION_JSON_VALUE)
    public Station getStationByName(@PathVariable String stationName) {
        return stationsService.findByName(stationName)
                .orElseThrow(() -> StationException.stationNotFound(stationName));
    }

    @PatchMapping(value = "/installation/stations/{stationName}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public Station patchStation(@PathVariable String stationName,
                                @RequestBody @Valid StationUpdate update) {
        return stationsService.updateStation(stationName, update);
    }

    public static <T> Slice<T> sliceFor(List<T> content, Pageable pageable) {
        final boolean hasNext = content.size() >= pageable.getPageSize();
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
