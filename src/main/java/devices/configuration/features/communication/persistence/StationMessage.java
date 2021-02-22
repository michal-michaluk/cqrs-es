package devices.configuration.features.communication.persistence;

import devices.configuration.features.communication.Endpoint;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "station_messages")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "eventId")
class StationMessage {

    @Id
    @Column(nullable = false)
    private UUID eventId;
    @Column(nullable = false)
    private Instant time;
    @Column(nullable = false)
    private String station;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Endpoint endpoint;
    @Column(nullable = false)
    private String type;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;
}
