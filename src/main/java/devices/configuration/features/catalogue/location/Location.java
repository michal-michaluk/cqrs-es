package devices.configuration.features.catalogue.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "update")
@Accessors(chain = true)
@Embeddable
public class Location {
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotBlank
    private String houseNumber;
    @NotBlank
    private String postalCode;
    private String state;
    @NotBlank
    @Size(min = 2, max = 3)
    private String countryISO;
    @Valid
    @NotNull
    private GeoLocation coordinates;

    @Transient
    @JsonIgnore
    private boolean update = true;

    public Location setCountryISO(String countryISO) {
        this.countryISO = CountryIsoCodes.normalise(countryISO);
        return this;
    }

    public List<String> toAddressDescription() {
        return List.of(
                String.format("%s %s", street, houseNumber),
                String.format("%s %s", postalCode, city)
        );
    }
}
