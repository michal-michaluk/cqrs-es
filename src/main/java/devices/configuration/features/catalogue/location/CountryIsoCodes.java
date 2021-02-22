package devices.configuration.features.catalogue.location;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * This is a mapper from alpha-2 to alpha-3 of all country ISO codes as described in the ISO 3166 international standard.
 */
public class CountryIsoCodes {

    private static final Set<String> alpha3 = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3);

    public static String normalise(String code) {
        if (isBlank(code) || "UNKNOWN".equalsIgnoreCase(code)) {
            return null;
        }
        code = code.toUpperCase();
        if (code.length() == 3 && alpha3.contains(code)) {
            return code;
        }
        if (code.length() == 2) {
            try {
                return new Locale("", code).getISO3Country();
            } catch (MissingResourceException e) {
                throw new IllegalArgumentException(e);
            }
        }
        throw new IllegalArgumentException("not supported iso country code " + code);
    }
}
