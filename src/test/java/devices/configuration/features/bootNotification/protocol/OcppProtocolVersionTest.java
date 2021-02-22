package devices.configuration.features.bootNotification.protocol;

import org.junit.jupiter.api.Test;

import static devices.configuration.features.bootNotification.protocol.OcppProtocolVersion.*;
import static org.junit.jupiter.api.Assertions.*;

class OcppProtocolVersionTest {

    @Test
    public void shouldReturn1_2Version() {
        assertEquals(V_1_2, OcppProtocolVersion.of("1.2"));
        assertEquals(V_1_2, OcppProtocolVersion.of("1.2.0"));
        assertEquals(V_1_2, OcppProtocolVersion.of("1.2.1"));
        assertEquals(V_1_2, OcppProtocolVersion.of("1.2.a"));
        assertEquals(V_1_2, OcppProtocolVersion.of("1.2.abc"));
    }

    @Test
    public void shouldReturn1_5Version() {
        assertEquals(V_1_5, OcppProtocolVersion.of("1.5"));
        assertEquals(V_1_5, OcppProtocolVersion.of("1.5.0"));
        assertEquals(V_1_5, OcppProtocolVersion.of("1.5.1"));
        assertEquals(V_1_5, OcppProtocolVersion.of("1.5.a"));
        assertEquals(V_1_5, OcppProtocolVersion.of("1.5.abc"));
    }

    @Test
    public void shouldReturn1_6Version() {
        assertEquals(V_1_6, OcppProtocolVersion.of("1.6"));
        assertEquals(V_1_6, OcppProtocolVersion.of("1.6.0"));
        assertEquals(V_1_6, OcppProtocolVersion.of("1.6.1"));
        assertEquals(V_1_6, OcppProtocolVersion.of("1.6.a"));
        assertEquals(V_1_6, OcppProtocolVersion.of("1.6.abc"));
    }

    @Test
    public void shouldReturn2_0Version() {
        assertEquals(V_2_0, OcppProtocolVersion.of("2.0"));
        assertEquals(V_2_0, OcppProtocolVersion.of("2.0.0"));
        assertEquals(V_2_0, OcppProtocolVersion.of("2.0.1"));
        assertEquals(V_2_0, OcppProtocolVersion.of("2.0.a"));
        assertEquals(V_2_0, OcppProtocolVersion.of("2.0.abc"));
    }

    @Test
    public void shouldReturnIncorrectVersion() {
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("0"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("0.1"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("0.a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("0a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.0"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.1.1"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.1.a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.1a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("11.11"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.50"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.5a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.60"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("1.6a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("2"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("2.1"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("2.a"));
        assertEquals(OcppProtocolVersion.INVALID, OcppProtocolVersion.of("2.0a"));
    }

    @Test
    public void shouldReturnIfIsValid() {
        assertTrue(V_1_2.isValid());
        assertTrue(V_1_5.isValid());
        assertTrue(V_1_6.isValid());
        assertTrue(V_2_0.isValid());
        assertFalse(INVALID.isValid());
        assertFalse(of("invalid").isValid());
        assertFalse(of("").isValid());
        assertFalse(of("abc").isValid());
    }
}
