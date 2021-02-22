package devices.configuration.features.communication;

import devices.configuration.features.eggplant.EmobilityOldPlatformClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
public class LegacySystem {

    private static final Logger log = LoggerFactory.getLogger("features.communication.legacy");

    private final EmobilityOldPlatformClient client;
    private final ExecutorService io = Executors.newFixedThreadPool(16);

    public void bootNotification(Endpoint endpoint, String station, Object request) {
        send(endpoint, "bootnotification", station, request);
    }

    public void statusNotification(Endpoint endpoint, String station, Object request) {
        send(endpoint, "statusNotification", station, request);
    }

    private void send(Endpoint endpoint, String actionName, String station, Object request) {
        CompletableFuture.supplyAsync(() -> client.sendOcppMessage(endpoint.getLegacyPath(), actionName, station, request), io)
                .thenAccept(response -> logSuccess(station, request, response))
                .exceptionally(throwable -> logError(station, request, throwable));
    }

    private void logSuccess(String station, Object request, ResponseEntity<String> response) {
        log.debug("Forwarding OCPP message to eggplant {}, {} and got response: {} {}", station, request, response.getStatusCode(), response.getBody());
    }

    private Void logError(String station, Object request, Throwable throwable) {
        log.error("Forwarding OCPP message to eggplant {}, {} and got exception", station, request, throwable);
        return null;
    }
}
