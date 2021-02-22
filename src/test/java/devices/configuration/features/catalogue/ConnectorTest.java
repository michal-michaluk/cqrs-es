package devices.configuration.features.catalogue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ConnectorTest {

    @Test
    void Should_calculate_AC_1phase_power() {
        Connector connector = ac("230.0", "16.0", "1");
        Assertions.assertThat(connector.getPowerInWatts())
                .isEqualTo(new BigDecimal("3680"));
    }

    @Test
    void Should_represent_advertised_power_of_AC_1phase() {
        Connector connector = ac("230.0", "16.0", "1");
        Assertions.assertThat(connector.getAdvertisedPower())
                .isEqualTo("3.7kW");
    }

    @Test
    void Should_calculate_AC_3phase_power() {
        Connector connector = ac("230.0", "16.0", "3");
        Assertions.assertThat(connector.getPowerInWatts())
                .isEqualTo(new BigDecimal("11040"));
    }

    @Test
    void Should_represent_advertised_power_of_AC_3phase() {
        Connector connector = ac("230.0", "16.0", "3");
        Assertions.assertThat(connector.getAdvertisedPower())
                .isEqualTo("11kW");
    }

    @Test
    void Should_calculate_DC_power() {
        Connector connector = dc("400.0", "64.0");
        Assertions.assertThat(connector.getPowerInWatts())
                .isEqualTo(new BigDecimal("25600"));
    }

    @Test
    void Should_represent_advertised_power_of_DC_2digits() {
        Connector connector = dc("400.0", "64.0");
        Assertions.assertThat(connector.getAdvertisedPower())
                .isEqualTo("26kW");
    }

    @Test
    void Should_represent_advertised_power_of_DC_3digits() {
        Connector connector = dc("600.0", "400.0");
        Assertions.assertThat(connector.getAdvertisedPower())
                .isEqualTo("240kW");
    }

    private Connector ac(String voltage, String amps, String phases) {
        return new Connector()
                .setAc(true)
                .setDc(false)
                .setVoltage(voltage)
                .setAmps(amps)
                .setPhases(phases);
    }

    private Connector dc(String voltage, String amps) {
        return new Connector()
                .setAc(false)
                .setDc(true)
                .setVoltage(voltage)
                .setAmps(amps)
                .setPhases(null);
    }
}