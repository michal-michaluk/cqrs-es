package devices.configuration.configs;

import devices.configuration.remote.IntervalRules;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "features_configuration")
@NoArgsConstructor
@AllArgsConstructor
public class FeaturesConfigurationEntity {
    @Id
    private UUID eventId;
    private Instant time;
    private String name;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private IntervalRules configuration;
}