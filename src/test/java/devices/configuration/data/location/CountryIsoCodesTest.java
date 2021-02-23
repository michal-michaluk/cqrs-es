package devices.configuration.data.location;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryIsoCodesTest {

    @Test
    void Should_normalise_correct_input() {
        assertResultsInPOL("pl");
        assertResultsInPOL("PL");
        assertResultsInPOL("POL");
        assertResultsInPOL("pol");
    }

    @Test
    void Should_nullify_blank_or_unknown_input() {
        assertNull(null);
        assertNull("");
        assertNull(" ");
        assertNull("UNKNOWN");
        assertNull("unknown");
    }

    @Test
    void Should_throw_Illegal_Argument_Exception_for_incorrect_input() {
        assertThrowsIllegalArgumentException("ab");
        assertThrowsIllegalArgumentException("abb");
    }

    private void assertResultsInPOL(String input) {
        assertThat(CountryIsoCodes.normalise(input)).isEqualTo("POL");
    }

    private void assertNull(String input) {
        assertThat(CountryIsoCodes.normalise(input)).isNull();
    }

    private void assertThrowsIllegalArgumentException(String input) {
        assertThrows(IllegalArgumentException.class, () -> CountryIsoCodes.normalise(input));
    }

}