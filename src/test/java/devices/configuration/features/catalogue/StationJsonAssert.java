package devices.configuration.features.catalogue;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.location.OpeningHours;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class StationJsonAssert {
    private final Object response;

    public static StationJsonAssert assertThat(Object response) {
        return new StationJsonAssert(response);
    }

    private String getStationName() {
        return JsonPath.read(response, "$.name");
    }

    public StationJsonAssert hasStationName(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.name"))
                .as("$.name of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasPhysicalReference(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.physicalReference"))
                .as("$.physicalReference of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasNumberOfOutlets(Integer expected) {
        Assertions.assertThat(JsonPath.<Integer>read(response, "$.numberOfOutlets"))
                .as("$.numberOfOutlets of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasMaxNumberOfOutlets(Integer expected) {
        Assertions.assertThat(JsonPath.<Integer>read(response, "$.maxNumberOfOutlets"))
                .as("$.maxNumberOfOutlets of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasVendor(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.vendor"))
                .as("$.vendor of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasProduct(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.product"))
                .as("$.product of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasProductDetails(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.productDetails"))
                .as("$.productDetails of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasColor(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.color"))
                .as("$.color of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasComment1(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.comment1"))
                .as("$.comment1 of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasComment2(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.comment2"))
                .as("$.comment2 of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesAvailableOcppVersions(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.capabilities.availableOcppVersions"))
                .as("$.capabilities.availableOcppVersions of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesDebitPayment(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.debitPayment"))
                .as("$.capabilities.debitPayment of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesReservation(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.reservation"))
                .as("$.capabilities.reservation of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesCreditPayment(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.creditPayment"))
                .as("$.capabilities.creditPayment of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesRfidReader(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.rfidReader"))
                .as("$.capabilities.rfidReader of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesRemoteStart(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.remoteStart"))
                .as("$.capabilities.remoteStart of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesScnDlb(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.scnDlb"))
                .as("$.capabilities.scnDlb of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesTokenGrouping(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.tokenGrouping"))
                .as("$.capabilities.tokenGrouping of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesSmartCharging(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.smartCharging"))
                .as("$.capabilities.smartCharging of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesUnlock(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.unlock"))
                .as("$.capabilities.unlock of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesAc(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.ac"))
                .as("$.capabilities.ac of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasCapabilitiesDc(Boolean expected) {
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.capabilities.dc"))
                .as("$.capabilities.dc of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasConnectors(Connector... connectors) {
        for (int i = 0; i < connectors.length; i++) {
            Connector expected = connectors[i];
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].name"))
                    .as("$.connectors[" + i + "].name of station %s", getStationName())
                    .isEqualTo(expected.getName());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].physicalReference"))
                    .as("$.connectors[" + i + "].physicalReference of station %s", getStationName())
                    .isEqualTo(expected.getPhysicalReference());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].evseId"))
                    .as("$.connectors[" + i + "].evseId of station %s", getStationName())
                    .isEqualTo(expected.getEvseId());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].type"))
                    .as("$.connectors[" + i + "].type of station %s", getStationName())
                    .isEqualTo(expected.getType().toString());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].format"))
                    .as("$.connectors[" + i + "].format of station %s", getStationName())
                    .isEqualTo("" + expected.getFormat());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].voltage"))
                    .as("$.connectors[" + i + "].voltage of station %s", getStationName())
                    .isEqualTo(expected.getVoltage());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].phases"))
                    .as("$.connectors[" + i + "].phases of station %s", getStationName())
                    .isEqualTo(expected.getPhases());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].amps"))
                    .as("$.connectors[" + i + "].amps of station %s", getStationName())
                    .isEqualTo(expected.getAmps());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].ac"))
                    .as("$.connectors[" + i + "].ac of station %s", getStationName())
                    .isEqualTo(expected.isAc());
            Assertions.assertThat(JsonPath.<Object>read(response, "$.connectors[" + i + "].dc"))
                    .as("$.connectors[" + i + "].dc of station %s", getStationName())
                    .isEqualTo(expected.isDc());
        }
        return this;
    }

    public StationJsonAssert hasAddedOnNotNull() {
        Assertions.assertThat(JsonPath.<Object>read(response, "$.addedOn"))
                .as("$.addedOn of station %s", getStationName())
                .isNotNull();
        return this;
    }

    public StationJsonAssert hasImageId(String expected) {
        Assertions.assertThat(JsonPath.<String>read(response, "$.imageId"))
                .as("$.imageId of station %s", getStationName())
                .isEqualTo(expected);
        return this;
    }

    public StationJsonAssert hasNoLocation() {
        Assertions.assertThat(JsonPath.<Object>read(response, "$.location")).isNull();
        return this;
    }

    public StationJsonAssert hasLocation(Location location) {
        Assertions.assertThat(JsonPath.<Object>read(response, "$.location"))
                .as("$.location of station %s", getStationName())
                .isNotNull();
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.city"))
                .as("$.location.city of station %s", getStationName())
                .isEqualTo(location.getCity());
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.street"))
                .as("$.location.street of station %s", getStationName())
                .isEqualTo(location.getStreet());
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.postalCode"))
                .as("$.location.postalCode of station %s", getStationName())
                .isEqualTo(location.getPostalCode());
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.state"))
                .as("$.location.state of station %s", getStationName())
                .isEqualTo(location.getState());
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.countryISO"))
                .as("$.location.countryISO of station %s", getStationName())
                .isEqualTo(location.getCountryISO());
        Assertions.assertThat(JsonPath.<Object>read(response, "$.location.coordinates")).isNotNull();
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.coordinates.latitude"))
                .as("$.location.coordinates.latitude of station %s", getStationName())
                .isEqualTo(location.getCoordinates().getLatitude());
        Assertions.assertThat(JsonPath.<String>read(response, "$.location.coordinates.longitude"))
                .as("$.location.coordinates.longitude of station %s", getStationName())
                .isEqualTo(location.getCoordinates().getLongitude());
        return this;
    }

    public StationJsonAssert hasDefaultOpeningHours() {
        hasOpeningHours(OpeningHours.alwaysOpen());
        return this;
    }

    public StationJsonAssert hasOpeningHours(OpeningHours openingHours) {
        Assertions.assertThat(JsonPath.<Object>read(response, "$.openingHours"))
                .as("$.openingHours of station %s", getStationName())
                .isNotNull();
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.alwaysOpen"))
                .as("$.openingHours.alwaysOpen of station %s", getStationName())
                .isEqualTo(openingHours.isAlwaysOpen());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.monday.open24h"))
                .as("$.openingHours.opened.monday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getMonday().isOpen24h());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.tuesday.open24h"))
                .as("$.openingHours.opened.tuesday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getTuesday().isOpen24h());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.wednesday.open24h"))
                .as("$.openingHours.opened.wednesday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getWednesday().isOpen24h());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.thursday.open24h"))
                .as("$.openingHours.opened.thursday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getThursday().isOpen24h());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.friday.open24h"))
                .as("$.openingHours.opened.friday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getFriday().isOpen24h());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.saturday.open24h"))
                .as("$.openingHours.opened.saturday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getSaturday().isOpen24h());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.sunday.open24h"))
                .as("$.openingHours.opened.sunday.open24h of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getSunday().isOpen24h());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.monday.closed"))
                .as("$.openingHours.opened.monday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getMonday().isClosed());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.tuesday.closed"))
                .as("$.openingHours.opened.tuesday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getTuesday().isClosed());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.wednesday.closed"))
                .as("$.openingHours.opened.wednesday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getWednesday().isClosed());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.thursday.closed"))
                .as("$.openingHours.opened.thursday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getThursday().isClosed());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.friday.closed"))
                .as("$.openingHours.opened.friday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getFriday().isClosed());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.saturday.closed"))
                .as("$.openingHours.opened.saturday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getSaturday().isClosed());
        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.openingHours.opened.sunday.closed"))
                .as("$.openingHours.opened.sunday.closed of station %s", getStationName())
                .isEqualTo(openingHours.getOpened().getSunday().isClosed());

        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.monday.open"))
                .as("$.openingHours.opened.monday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getMonday().getOpen()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.tuesday.open"))
                .as("$.openingHours.opened.tuesday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getTuesday().getOpen()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.wednesday.open"))
                .as("$.openingHours.opened.wednesday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getWednesday().getOpen()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.thursday.open"))
                .as("$.openingHours.opened.thursday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getThursday().getOpen()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.friday.open"))
                .as("$.openingHours.opened.friday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getFriday().getOpen()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.saturday.open"))
                .as("$.openingHours.opened.saturday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getSaturday().getOpen()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.sunday.open"))
                .as("$.openingHours.opened.sunday.open of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getSunday().getOpen()));

        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.monday.close"))
                .as("$.openingHours.opened.monday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getMonday().getClose()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.tuesday.close"))
                .as("$.openingHours.opened.tuesday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getTuesday().getClose()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.wednesday.close"))
                .as("$.openingHours.opened.wednesday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getWednesday().getClose()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.thursday.close"))
                .as("$.openingHours.opened.thursday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getThursday().getClose()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.friday.close"))
                .as("$.openingHours.opened.friday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getFriday().getClose()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.saturday.close"))
                .as("$.openingHours.opened.saturday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getSaturday().getClose()));
        Assertions.assertThat(JsonPath.<String>read(response, "$.openingHours.opened.sunday.close"))
                .as("$.openingHours.opened.sunday.close of station %s", getStationName())
                .isEqualTo(asString(openingHours.getOpened().getSunday().getClose()));

        return this;
    }

    private Object asString(LocalTime time) {
        return time == null ? null : time.format(DateTimeFormatter.ISO_TIME);
    }

    public StationJsonAssert hasSettings(Settings settings) {
        Assertions.assertThat(JsonPath.<Object>read(response, "$.settings"))
                .as("$.settings of station %s", getStationName())
                .isNotNull();

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.settings.autoStart"))
                .as("$.settings.autoStart of station %s", getStationName())
                .isEqualTo(settings.getAutoStart());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.settings.remoteControl"))
                .as("$.settings.remoteControl of station %s", getStationName())
                .isEqualTo(settings.getRemoteControl());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.settings.billing"))
                .as("$.settings.billing of station %s", getStationName())
                .isEqualTo(settings.getBilling());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.settings.reimbursement"))
                .as("$.settings.reimbursement of station %s", getStationName())
                .isEqualTo(settings.getReimbursement());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.settings.showOnMap"))
                .as("$.settings.showOnMap of station %s", getStationName())
                .isEqualTo(settings.getShowOnMap());

        Assertions.assertThat(JsonPath.<Boolean>read(response, "$.settings.publicAccess"))
                .as("$.settings.publicAccess of station %s", getStationName())
                .isEqualTo(settings.getPublicAccess());

        return this;
    }
}
