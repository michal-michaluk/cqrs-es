package devices.configuration.legacy.stationImport;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StationViewTest {

    @Test
    void Should_thrown_not_supported_exception_when_plug_type_is_not_known() {
        // given
        StationView.ChargingStation stationView = StationViewFixture.stationViewWithPlugType("stationWithUNSUPPORTEDPlug", "UNSUPPORTED");

        // expected
        Assertions.assertThatThrownBy(stationView::toConnectors)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Given plugType: UNSUPPORTED is not supported");
    }

}
