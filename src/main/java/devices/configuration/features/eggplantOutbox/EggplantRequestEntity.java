package devices.configuration.features.eggplantOutbox;

import devices.configuration.features.bootNotification.BootNotificationFields;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Data
public class EggplantRequestEntity {
    @Id
    @GeneratedValue
    private Long entityId;

    private String stationName;

    private String softwareVersion;
    private String protocolName;
    private String protocolVersion;

    private Instant createdAt;

    public static EggplantRequestEntity of(String stationName, BootNotificationFields bootNotificationFields, Instant now) {
        EggplantRequestEntity eggplantRequestEntity = new EggplantRequestEntity();
        eggplantRequestEntity.stationName = stationName;
        eggplantRequestEntity.softwareVersion = bootNotificationFields.getSoftwareVersion();
        eggplantRequestEntity.protocolName = bootNotificationFields.getProtocolName();
        eggplantRequestEntity.protocolVersion = bootNotificationFields.getProtocolVersion();
        eggplantRequestEntity.createdAt = now;
        return eggplantRequestEntity;
    }
}
