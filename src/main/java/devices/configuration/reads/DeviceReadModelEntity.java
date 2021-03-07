package devices.configuration.reads;

import devices.configuration.device.DeviceSnapshot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Version;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "devices_snapshot")
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReadModelEntity {
    @Id
    private String deviceId;
    private String operator;
    private String provider;
    @Version
    private Long version;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DeviceSnapshot snapshot;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private CrmAccount operatorDetails;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private CrmAccount providerDetails;

    public DeviceReadModelEntity(String deviceId) {
        this.deviceId = deviceId;
    }
}
