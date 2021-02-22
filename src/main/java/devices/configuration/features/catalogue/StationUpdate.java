package devices.configuration.features.catalogue;

import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.location.OpeningHours;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@Data
@Accessors(chain = true)
public class StationUpdate {
    @Valid
    Location location;
    @Valid
    OpeningHours openingHours;
    @Valid
    Settings settings;
    @Valid
    Ownership ownership;

    public StationUpdate onLocationUpdate(Consumer<Location> consumer) {
        ofNullable(location).ifPresent(consumer);
        return this;
    }

    public StationUpdate onOpeningUpdate(Consumer<OpeningHours> consumer) {
        ofNullable(openingHours).ifPresent(consumer);
        return this;
    }

    public StationUpdate onOwnershipUpdate(Consumer<Ownership> consumer) {
        ofNullable(ownership).ifPresent(consumer);
        return this;
    }

    public StationUpdate onSettingsUpdate(Consumer<Settings> consumer) {
        ofNullable(settings).ifPresent(consumer);
        return this;
    }
}
