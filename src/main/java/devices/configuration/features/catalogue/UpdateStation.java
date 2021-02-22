package devices.configuration.features.catalogue;

import devices.configuration.features.catalogue.location.GeoLocation;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.location.OpeningHours;
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

    public StationUpdate toPreviousForm() {
        StationUpdate update = new StationUpdate();
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
