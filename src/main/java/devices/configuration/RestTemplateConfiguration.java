package devices.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static java.time.Duration.ofSeconds;

@Configuration
class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate(
            @Value("${emobility.client.read-timeout:5}") int readTimeout,
            @Value("${emobility.client.connect-timeout:5}") int connectTimeout,
            RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setReadTimeout(ofSeconds(readTimeout))
                .setConnectTimeout(ofSeconds(connectTimeout))
                .build();
    }
}
