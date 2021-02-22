package devices.configuration.outbox;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import devices.configuration.DomainEvent;
import devices.configuration.EventTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Table(name = "outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "eventId")
class OutboxMessage {

    @Id
    private UUID eventId;
    private Instant occurrenceTime;
    private String type;
    private String version;
    @JsonUnwrapped
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DomainEvent payload;

    public OutboxMessage(UUID eventId, Instant occurrenceTime, EventTypes.Type type, DomainEvent event) {
        this.eventId = eventId;
        this.occurrenceTime = occurrenceTime;
        this.type = type.getType();
        this.version = type.getVersion();
        this.payload = event;
    }

    public <T extends DomainEvent> T getPayload(Class<T> type) {
        return type.cast(payload);
    }
}
