package devices.configuration.features.bootNotification.protocol;

import org.junit.jupiter.api.Test;

import static devices.configuration.features.bootNotification.protocol.EggplantProtocolName.*;
import static org.junit.jupiter.api.Assertions.*;

class EggplantProtocolNameTest {

    @Test
    public void shouldReturnOcppProtocol() {
        assertEquals(OCPP, of("ocpp"));
        assertEquals(OCPP, of("ocPp"));
    }

    @Test
    public void shouldReturnOcpp_JProtocol() {
        assertEquals(OCPP_J, of("ocpp-j"));
        assertEquals(OCPP_J, of("ocPp-J"));
    }

    @Test
    public void shouldReturnOcpp_AZUREProtocol() {
        assertEquals(OCPP_AZURE, of("ocpp-azure"));
        assertEquals(OCPP_AZURE, of("ocPp-AzUre"));
    }

    @Test
    public void shouldReturnInvalidProtocol() {
        assertEquals(INVALID, of("invalid"));
        assertEquals(INVALID, of(""));
        assertEquals(INVALID, of("abc"));
    }

    @Test
    public void shouldReturnIfIsValid() {
        assertTrue(OCPP.isValid());
        assertTrue(OCPP_J.isValid());
        assertTrue(OCPP_AZURE.isValid());
        assertFalse(INVALID.isValid());
        assertFalse(of("").isValid());
        assertFalse(of("abc").isValid());
        assertFalse(of("invalid").isValid());
    }
}
