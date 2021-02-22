package devices.configuration.maintenance;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

class ApiKeys {
    private static final String ours =
            "RR:d65d297a-427a-476e-87e1-b723c8b0c5c8";

    private static final Set<String> all = Set.of(ours,
            "ATeam:1d369a73-3260-4029-aaaa-b8171db3aa1c",
            "AppTeam:f8dd58e1-fa5b-45f5-91d3-948cceff100c"
    );

    static void requireOurs(String apiKey) {
        requireIn(Set.of(ours), apiKey);
    }

    static void requireAny(String apiKey) {
        requireIn(all, apiKey);
    }

    private static void requireIn(Set<String> ours, String apiKey) {
        if (apiKey == null || !ours.contains(apiKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "correct apiKey required");
        }
    }

    private ApiKeys() {
    }
}
