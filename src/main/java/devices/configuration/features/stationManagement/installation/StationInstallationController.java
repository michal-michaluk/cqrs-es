package devices.configuration.features.stationManagement.installation;

import de.vattenfall.emobility.token.EmobilityAuthentication;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@Slf4j
public class StationInstallationController {

    private final StationInstaller stationInstallation;

    @PutMapping("/registration/stations/{stationName}")
    @ResponseStatus(CREATED)
    public StationRegistration connectNewStation(@PathVariable String stationName, @RequestBody InstallStation request, @ApiIgnore EmobilityAuthentication auth) {
        return stationInstallation.install(stationName, request, auth);
    }

    @PatchMapping("/registration/stations/{stationName}")
    public void assignLcpToStation(@PathVariable String stationName, @ApiIgnore EmobilityAuthentication auth) {
        stationInstallation.finalize(stationName, auth);
    }
}
