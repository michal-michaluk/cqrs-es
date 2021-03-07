package devices.configuration.device.persistence;

import devices.configuration.DomainEvent;
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
@Table(name = "device_events")
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEventEntity {
    @Id
    private UUID eventId;
    private Instant time;
    private String deviceId;
    private String type;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DomainEvent event;
}
