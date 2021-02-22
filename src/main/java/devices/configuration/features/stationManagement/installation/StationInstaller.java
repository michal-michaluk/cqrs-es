package devices.configuration.features.stationManagement.installation;

import devices.configuration.features.catalogue.Ownership;
import devices.configuration.features.catalogue.StationUpdate;
import devices.configuration.features.catalogue.StationsService;
import devices.configuration.features.stationManagement.StationManagementService;
import de.vattenfall.emobility.token.EmobilityAuthentication;
import de.vattenfall.emobility.token.authority.SubjectBasedAuthority;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Supplier;

import static devices.configuration.features.stationManagement.installation.StationInstaller.ErrorStatus.*;
import static org.springframework.http.HttpStatus.*;

@Service
@AllArgsConstructor
public
class StationInstaller {

    private final StationManagementService stationManagement;
    private final StationInstallationRegistry registry;
    private final StationsService stationsService;

    @Transactional
    public StationRegistration install(String stationName, InstallStation installStation, EmobilityAuthentication authentication) {
        String cpo = installStation.getCpo();
        validateUserIs(cpo, authentication);
        
        StationRegistration registration = registerInstallationRequest(stationName, authentication, installStation.toOwnership());

        stationManagement.generateStationCreatedEvent(stationName);

        stationManagement.generateStationCpoUpdated(stationName, cpo);

        if (installStation.isSingleStepInstallation()) {
            assignToLcp(stationName, installStation.toOwnership());
        }

        return registration;
    }

    void finalize(String stationName, EmobilityAuthentication authentication) {
        Ownership ownership = registry.findById(stationName)
                .map(StationRegistration::getOwnership)
                .orElseThrow(firstStepNotFound())
                .withLcp(authority(authentication, "LCP").stream()
                        .findFirst()
                        .orElseThrow(internalServerError(authentication)));

        assignToLcp(stationName, ownership);
    }

    private StationRegistration registerInstallationRequest(String stationName, EmobilityAuthentication auth, Ownership ownership) {
        registry.deleteAllByStationName(stationName);

        return registry.save(
                StationRegistration.builder()
                        .stationName(stationName)
                        .userName(auth.getDisplayName())
                        .ownership(ownership)
                        .build());
    }

    private void validateUserIs(String cpo, EmobilityAuthentication authentication) {
        authority(authentication, "CPO").stream()
                .filter(cpoName -> cpoName.equals(cpo))
                .findFirst()
                .orElseThrow(userIsNotCpo(cpo, authentication));
    }

    private void assignToLcp(String stationName, Ownership ownership) {
        stationsService.updateStation(stationName, new StationUpdate().setOwnership(ownership));
        stationManagement.generateStationInstalledToLcp(stationName, ownership.getLcp());
        registry.deleteById(stationName);
    }

    private List<String> authority(EmobilityAuthentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .filter(a -> a.getAuthority().equals(authority))
                .map(SubjectBasedAuthority.class::cast)
                .findFirst()
                .orElseThrow(getForbidden(authority))
                .getSubjects();
    }

    static class ErrorStatus {
        static Supplier<ResponseStatusException> firstStepNotFound() {
            return status(NOT_FOUND, "Unable to finalize station installation without first step");
        }

        static Supplier<ResponseStatusException> internalServerError(EmobilityAuthentication authentication) {
            return status(INTERNAL_SERVER_ERROR, "User " + authentication.getDisplayName() + " has no LCP subjects in token!");
        }

        static Supplier<ResponseStatusException> userIsNotCpo(String cpo, EmobilityAuthentication authentication) {
            return status(FORBIDDEN, "Provided CPO " + cpo + " is not in the token of user " + authentication.getDisplayName());
        }

        static Supplier<ResponseStatusException> getForbidden(String authority) {
            return status(FORBIDDEN, "User has no authority " + authority);
        }

        private static Supplier<ResponseStatusException> status(HttpStatus status, String reason) {
            return () -> new ResponseStatusException(status, reason);
        }
    }

}
