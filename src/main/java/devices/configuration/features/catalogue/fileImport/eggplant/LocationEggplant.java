package devices.configuration.features.catalogue.fileImport.eggplant;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationEggplant {
    private String city;
    private String zipcode;
    private String street;
    private String houseNumber;
    private String district;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String countryISO;
}
