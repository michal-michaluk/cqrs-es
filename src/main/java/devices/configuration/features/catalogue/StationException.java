package devices.configuration.features.catalogue;

import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

public class StationException {

    public static ResponseStatusException photoNotFound(String name, UUID photoId) {
        return photoNotFound(name, photoId.toString());
    }

    public static ResponseStatusException photoNotFound(String name, String photoId) {
        return new ResponseStatusException(NOT_FOUND, "Station " + name + " has no photo with ID " + photoId);
    }

    public static ResponseStatusException stationNotFound(String stationName) {
        return new ResponseStatusException(NOT_FOUND, ("Could not find Station with given name " + stationName + "."));
    }

    public static ResponseStatusException blobConnectionError(String reason) {
        return new ResponseStatusException(INTERNAL_SERVER_ERROR, reason);
    }

    public static ResponseStatusException requiredCpoAuthority() {
        return new ResponseStatusException(FORBIDDEN, "User has no CPO authority");
    }

    public static ResponseStatusException requiredLcpAuthority() {
        return new ResponseStatusException(FORBIDDEN, "User has no LCP authority");
    }
}
