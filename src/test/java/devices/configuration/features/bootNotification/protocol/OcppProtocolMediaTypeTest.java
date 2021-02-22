package devices.configuration.features.bootNotification.protocol;

import org.junit.jupiter.api.Test;

import static devices.configuration.features.bootNotification.protocol.OcppProtocolMediaType.*;
import static org.junit.jupiter.api.Assertions.*;

class OcppProtocolMediaTypeTest {

    @Test
    public void shouldReturnJsonMediaType() {
        assertEquals(JSON, of("json"));
        assertEquals(JSON, of("Json"));
        assertEquals(JSON, of("jSon"));
        assertEquals(JSON, of("jsOn"));
        assertEquals(JSON, of("jsoN"));
        assertEquals(JSON, of("JSON"));
    }

    @Test
    public void shouldReturnSoapMediaType() {
        assertEquals(SOAP, of("soap"));
        assertEquals(SOAP, of("Soap"));
        assertEquals(SOAP, of("sOap"));
        assertEquals(SOAP, of("soAp"));
        assertEquals(SOAP, of("soaP"));
        assertEquals(SOAP, of("SOAP"));
    }

    @Test
    public void shouldReturnIfIsValid() {
        assertTrue(SOAP.isValid());
        assertTrue(JSON.isValid());
        assertFalse(INVALID.isValid());
        assertFalse(of("invalid").isValid());
        assertFalse(of("").isValid());
        assertFalse(of("abc").isValid());
    }
}
