package devices.configuration.features.bootNotification;

import devices.configuration.DomainEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static devices.configuration.features.bootNotification.ChargingStationManagementSystem.CHARGING_STATION_COMMUNICATION;
import static devices.configuration.features.bootNotification.ChargingStationManagementSystem.OLD_PLATFORM;

@Entity
@Data
@NoArgsConstructor
class StationsCatalogueEntity {

    private static final Logger log = LoggerFactory.getLogger(StationsCatalogueEntity.class);

    @Id
    @GeneratedValue
    private Long entityId;

    @Column(unique = true)
    @NotNull
    private String stationName;
    private String protocolName;
    private String protocolVersion;
    private String mediaType;
    private String csms;
    private String softwareVersion;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    StationsCatalogueEntity(String stationName) {
        this.stationName = stationName;
    }

    void applyNewProtocol(String protocolName, String protocolVersion, String mediaType) {
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
        this.mediaType = mediaType;
        this.events.add(new StationProtocolChanged(this.stationName, this.protocolName, this.protocolVersion, this.mediaType));
    }

    void applyNewCsms(String newCsms) {
        this.csms = newCsms;
        if (CHARGING_STATION_COMMUNICATION.name.equals(this.csms)) {
            events.add(new StationConnectedToCsc(stationName));
        } else if (OLD_PLATFORM.name.equals(this.csms)) {
            events.add(new StationConnectedToOldPlatform(stationName));
        }
    }

    @Transient
    private List<DomainEvent> events = new ArrayList<>();

    @DomainEvents
    public Collection<DomainEvent> events() {
        return events;
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        events.clear();
    }
}
