package devices.configuration.features.catalogue.photo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URL;
import java.util.UUID;

@Entity
@Data
@Accessors(chain = true)
public class StationPhoto {
    @Id
    @GeneratedValue
    private UUID id;

    private URL url;

    private String name;

    private String category;
}
