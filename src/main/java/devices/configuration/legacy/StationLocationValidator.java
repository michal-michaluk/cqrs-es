package devices.configuration.legacy;

import devices.configuration.features.catalogue.location.Location;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class StationLocationValidator {

    public static final String DEFAULT_POSTAL_CODE = "0000AA";
    public static final String UNKNOWN = "UNKNOWN";

    public String issues(Throwable throwable, Location location) {
        return Stream.of(
                Optional.ofNullable(throwable).map(Throwable::getMessage).stream(),
                validate(location),
                hasIllegalValues(location)
        ).flatMap(Function.identity()).collect(Collectors.joining(", "));
    }

    private Stream<String> validate(Location location) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(location).stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage());
    }

    private Stream<String> hasIllegalValues(Location location) {
        if (hasValue(location.getPostalCode(), DEFAULT_POSTAL_CODE) ||
                hasValue(location.getCity(), UNKNOWN) ||
                hasValue(location.getPostalCode(), UNKNOWN) ||
                hasValue(location.getStreet(), UNKNOWN)) {
            return Stream.of("Not allowed \"0000AA\" or \"UNKNOWN\" values.");
        } else {
            return Stream.empty();
        }
    }

    private boolean hasValue(String postalCode, String s) {
        return Optional.ofNullable(postalCode)
                .map(String::toUpperCase)
                .filter(v -> v.equals(s))
                .isPresent();
    }
}
