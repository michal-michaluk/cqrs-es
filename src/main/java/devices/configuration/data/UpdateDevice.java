package devices.configuration.data;

import devices.configuration.data.location.Location;
import devices.configuration.data.location.OpeningHours;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@Data
@Accessors(chain = true)
public class UpdateDevice {
    @Valid
    Location location;
    @Valid
    OpeningHours openingHours;
    @Valid
    Settings settings;
    @Valid
    Ownership ownership;

    public UpdateDevice onLocationUpdate(Consumer<Location> consumer) {
        ofNullable(location).ifPresent(consumer);
        return this;
    }

    public UpdateDevice onOpeningUpdate(Consumer<OpeningHours> consumer) {
        ofNullable(openingHours).ifPresent(consumer);
        return this;
    }

    public UpdateDevice onOwnershipUpdate(Consumer<Ownership> consumer) {
        ofNullable(ownership).ifPresent(consumer);
        return this;
    }

    public UpdateDevice onSettingsUpdate(Consumer<Settings> consumer) {
        ofNullable(settings).ifPresent(consumer);
        return this;
    }
}
