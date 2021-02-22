package devices.configuration;

import devices.configuration.blob.BlobStorage;
import devices.configuration.blob.FakeBlobStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("integration-test")
public class MockBlobStorageConfiguration {

    @Bean
    @Primary
    public BlobStorage blobStorage() {
        return new FakeBlobStorage();
    }
}