package devices.configuration.features.catalogue;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class Query {

    private final String query;

    private Query(String query) {
        this.query = query;
    }

    public static Query of(String query) {
        return new Query(query);
    }

    public Predicate<? super Station> stationContainsNameOrEvseIdLike() {
        if (StringUtils.isBlank(query)) {
            return station -> true;
        }
        return station ->
                containsIgnoringCases(station.getName()) ||
                        station.getConnectors().stream()
                                .anyMatch(c -> containsIgnoringCases(c.getEvseId()));
    }

    private boolean containsIgnoringCases(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return value.toUpperCase().contains(query.toUpperCase());
    }
}
