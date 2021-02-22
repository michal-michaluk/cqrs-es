package devices.configuration.features.communication.ocpp20;

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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@PactBroker(scheme = "https", host = "emob-pact.azurewebsites.net")
@Provider("station-configuration")
@PactFilter("boot notification 2.0.*")
public class Ocpp20PactsProviderTest {

    private final Clock clock = Clock.fixed(Instant.parse("2000-01-31T14:00:00.000Z"), ZoneId.of("Z"));
    private final BootIntervals intervals = Mockito.mock(BootIntervals.class);
    private final MessageRepository repository = Mockito.mock(MessageRepository.class);
    private final LegacySystem legacy = Mockito.mock(LegacySystem.class);
    private final Ocpp20Controller controller = new Ocpp20Controller(clock, intervals, repository, legacy);

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

    @State("boot notification 2.0 is accepted")
    public void bootNotificationAccepted() {
        Mockito.when(intervals.heartbeatInterval(Mockito.any())).thenReturn(100);
    }
}
