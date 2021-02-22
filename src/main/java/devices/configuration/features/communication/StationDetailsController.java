package devices.configuration.features.communication;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@AllArgsConstructor
@RestController
public class StationDetailsController {

    private final StationDetailsService stationDetailsService;

    @GetMapping("/stationDetails/{stationName}")
    public StationDetails getStationDetails(@PathVariable String stationName) {
        return stationDetailsService.getStationDetailByStationName(stationName)
                .orElseThrow(() -> stationDetailsNotFound(stationName));
    }

    private static ResponseStatusException stationDetailsNotFound(String stationName) {
        return new ResponseStatusException(NOT_FOUND, ("Could not find station details for given station name " + stationName + "."));
    }
}
