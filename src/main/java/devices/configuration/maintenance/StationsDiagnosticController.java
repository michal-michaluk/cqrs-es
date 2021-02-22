package devices.configuration.maintenance;

import devices.configuration.published.StationReadModelV1;
import devices.configuration.published.StationsReadModelController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
class StationsDiagnosticController {

    private final StationsReadModelController controller;

    @GetMapping(value = "/prv/stations", params = "version=1", produces = APPLICATION_JSON_VALUE)
    Page<StationReadModelV1> getStationsV1(Pageable pageable, String apiKey) {
        ApiKeys.requireAny(apiKey);
        return controller.getStationsV1(pageable);
    }

    @GetMapping(value = "/prv/stations/{name}", params = "version=1", produces = APPLICATION_JSON_VALUE)
    StationReadModelV1 getStationByNameV1(@PathVariable String name, String apiKey) {
        ApiKeys.requireAny(apiKey);
        return controller.getStationByNameV1(name);
    }
}
