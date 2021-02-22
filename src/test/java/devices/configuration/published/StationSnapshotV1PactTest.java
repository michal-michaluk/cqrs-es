package devices.configuration.published;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Consumer;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import com.google.common.collect.ImmutableMap;
import devices.configuration.IntegrationTest;
import devices.configuration.StationsFixture;
import devices.configuration.features.catalogue.Ownership;
import devices.configuration.features.catalogue.StationUpdate;
import devices.configuration.features.catalogue.StationsRepository;
import devices.configuration.features.catalogue.StationsService;
import devices.configuration.outbox.OutgoingEventsTestListener;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

import static devices.configuration.TestTransaction.transactional;
import static devices.configuration.outbox.OutgoingEventsTestListener.event;

@Disabled
@PactBroker(scheme = "https", host = "emob-pact.azurewebsites.net")
@Provider("station-configuration")
@Consumer("roaming-evse-exporter")
//@PactFilter("")
@IntegrationTest
@Transactional
public class StationSnapshotV1PactTest {

    @Autowired
    StationsRepository repository;
    @Autowired
    StationsService service;
    @Autowired
    OutgoingEventsTestListener emitted;

    String stationName = StationsFixture.randomStationName();

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    public void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget(List.of("com.vattenfall.emobility.sc")));
    }

    @State("StationSnapshotFromStationConfigurationEvent")
    public void givenStationEFACECQC0032() {
        transactional(() -> repository.findByName(stationName).ifPresent(repository::delete));
        transactional(() -> repository.save(devices.configuration.features.catalogue.StationsFixture.matchinPactExampleEFACECQC0032()
                .setName(stationName)
                .setLcp(null)
        ));
    }

    @SneakyThrows
    @PactVerifyProvider("StationSnapshotFromStationConfigurationEvent")
    public MessageAndMetadata verifySnapshotOfEFACECQC0032() {
        transactional(() ->
                service.updateStation(stationName, new StationUpdate().setOwnership(
                        Ownership.of("InCharge SE", "LCP xxx.xxx")
                ))
        );
        String last = emitted.waitOn(event()
                .ofType("StationSnapshot")
                .withVersion("1")
                .withStationName(stationName))
                .orElseThrow();
        System.out.println("FOUND: " + last);

        return new MessageAndMetadata(
                last.getBytes(),
                ImmutableMap.of(
                        "kafka_messageKey", stationName,
                        "kafka_topic", "station-configuration-station-snapshot-v1"
                )
        );
    }
}
