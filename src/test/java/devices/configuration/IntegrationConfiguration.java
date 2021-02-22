package devices.configuration;

import devices.configuration.features.Toggles;
import devices.configuration.features.toggle.NewToggle;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("integration-test")
@Configuration
class IntegrationConfiguration {

    static {
        var kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
                .withReuse(true)
                .withNetwork(null);
        kafkaContainer.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());

        var postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:9.6.12"))
                .withReuse(true);
        postgreSQLContainer.start();
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }

    @Bean
    @Scope("prototype")
    MockRestServiceServer mockServer(RestTemplate restTemplate) {
        return MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Bean
    NewTopic createEmobilityStationTopic() {
        return new NewTopic("emobility-station", 1, (short) 1);
    }

    @Bean
    NewTopic createLocationUpdatesTopic() {
        return new NewTopic("location-updates", 1, (short) 1);
    }

    @Bean
    NewTopic createStationConfigurationTopic() {
        return new NewTopic("station-configuration", 1, (short) 1);
    }

    @Bean
    NewTopic createEmobilityStationSnapshotTopic() {
        return new NewTopic("emobility-station-snapshot", 1, (short) 1);
    }

    @Bean
    NewTopic createStationSnapshotV1Topic() {
        return new NewTopic("station-configuration-station-snapshot-v1", 1, (short) 1);
    }

    @Bean
    List<NewToggle> createSendStationEventsToggle() {
        return Stream.of(Toggles.OLD_PLATFORM_BOOT_NOTIFICATION, Toggles.SYNC_LOCATIONUPDATED_FROM_EGGPLANT)
                .map(name -> new NewToggle(name, true))
                .collect(Collectors.toList());
    }
}
