package devices.configuration.features.stationManagement.installation;

import devices.configuration.features.catalogue.Ownership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationRegistration {
    @Id
    private String stationName;

    @Embedded
    private Ownership ownership;

    private String userName;

    @UpdateTimestamp
    private Instant time;
}
