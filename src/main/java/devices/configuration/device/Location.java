package devices.configuration.device;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Location {
    String street;
    String houseNumber;
    String city;
    String postalCode;
    String state;
    String country;
    Coordinates coordinates;

    @Value
    public static class Coordinates {
        BigDecimal longitude;
        BigDecimal latitude;
    }
}
