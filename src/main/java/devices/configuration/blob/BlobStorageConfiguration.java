package devices.configuration.blob;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BlobStorageConfiguration {

    @Bean
    BlobContainerClient blobContainerClient(
            @Value("${spring.azure.blob.store.connectionString}") String connectionString,
            @Value("${spring.azure.blob.store.containerName}") String containerName) {
        return new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
    }
}
