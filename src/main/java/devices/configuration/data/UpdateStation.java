package devices.configuration.data;

import devices.configuration.data.location.GeoLocation;
import devices.configuration.data.location.Location;
import devices.configuration.data.location.OpeningHours;
import lombok.Builder;

import javax.validation.Valid;

@Valid
@Builder
public class UpdateStation {
    @Valid
    StationReadModel.OwnershipSnapshot ownership;
    @Valid
    StationReadModel.LocationSnapshot location;
    @Valid
    OpeningHours openingHours;
    @Valid
    Settings settings;

    public UpdateDevice toPreviousForm() {
        UpdateDevice update = new UpdateDevice();
        if (ownership != null) {
            update.setOwnership(new Ownership(ownership.getCpo(), ownership.getLcp()));
        }
        if (location != null) {
            update.setLocation(new Location()
                    .setCity(location.getCity())
                    .setStreet(location.getStreet())
                    .setHouseNumber(location.getHouseNumber())
                    .setPostalCode(location.getPostalCode())
                    .setState(location.getState())
                    .setCountryISO(location.getCountry())
                    .setCoordinates(new GeoLocation()
                            .setLatitude(location.getCoordinates().getLatitude().toPlainString())
                            .setLongitude(location.getCoordinates().getLongitude().toPlainString()))
            );
        }
        if (openingHours != null) {
            update.setOpeningHours(openingHours);
        }
        if (settings != null) {
            update.setSettings(settings);
        }
        return update;
    }
}
