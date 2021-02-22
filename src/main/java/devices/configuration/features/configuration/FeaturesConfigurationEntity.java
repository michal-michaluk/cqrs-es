package devices.configuration.features.configuration;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "features_configuration")
@Data
@Slf4j
public class FeaturesConfigurationEntity {

    @Id
    private String name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private FeatureConfiguration configuration;

    public <T> T get(Class<T> type) {
        if (!type.isInstance(configuration)) {
            String message = "Expecting configuration of type" + type.getSimpleName() + "for configuration key" + name + ", but was" + configuration.getClass().getSimpleName();
            log.error(message);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }

        return type.cast(configuration);
    }
}
