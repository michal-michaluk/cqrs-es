package devices.configuration.features.communication.ocpp16;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFilter;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import devices.configuration.features.communication.BootIntervals;
import devices.configuration.features.communication.LegacySystem;
import devices.configuration.features.communication.persistence.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@PactBroker(scheme = "https", host = "emob-pact.azurewebsites.net")
@Provider("station-configuration")
@PactFilter("boot notification 1.6.*")
public class Ocpp16PactsProviderTest {

    private final Clock clock = Clock.fixed(Instant.parse("2019-07-16T09:28:45Z"), ZoneId.of("Z"));
    private final BootIntervals intervals = Mockito.mock(BootIntervals.class);
    private final MessageRepository repository = Mockito.mock(MessageRepository.class);
    private final LegacySystem legacy = Mockito.mock(LegacySystem.class);
    private final Ocpp16Controller controller = new Ocpp16Controller(clock, intervals, repository, legacy);

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    public void before(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(controller);
        context.setTarget(testTarget);
    }

    @State("boot notification 1.6 is accepted")
    public void bootNotificationAccepted() {
        Mockito.when(intervals.heartbeatInterval(Mockito.any())).thenReturn(600);
    }

    @State("boot notification 1.6 ends with error")
    public void bootNotificationError() {
        Mockito.when(intervals.heartbeatInterval(Mockito.any()))
                .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
