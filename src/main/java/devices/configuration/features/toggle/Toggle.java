package devices.configuration.features.toggle;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Toggle {

    @Id
    String name;
    boolean value;

    public boolean isEnabled() {
        return value;
    }
}
