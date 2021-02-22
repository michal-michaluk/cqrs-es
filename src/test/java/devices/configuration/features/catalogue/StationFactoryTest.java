package devices.configuration.features.catalogue;

import devices.configuration.util.CsvReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

class StationFactoryTest {

    @Mock
    private CsvReader.Line csvLineMock;

    @Mock
    private StationImageProperties properties;

    @InjectMocks
    private StationFactory stationFactory;

    @BeforeEach
    public void setup() {
        initMocks(this);

        doReturn(32).when(csvLineMock).getNumberOfColumns();
        givenMaxNumberOfOutletsIsSet();
    }

    @Test
    public void Should_accept_correct_connector_type() {
        // given
        givenOneOutletProvided();
        givenConnectorType(ConnectorType.TYPE_1_SAE_J1772.toString());

        // when
        Station station = stationFactory.createStation(csvLineMock);

        // then
        assertThat(station.getConnectors().get(0).getType()).isEqualTo(ConnectorType.TYPE_1_SAE_J1772);
    }

    @Test
    public void Should_throw_for_unknown_connector_type() {
        // given
        givenOneOutletProvided();
        givenConnectorType("some inappropriate type");

        // expected
        assertThrows(IllegalArgumentException.class, () -> {
            stationFactory.createStation(csvLineMock);
        });
    }

    @Test
    public void Should_throw_for_connector_type_being_empty() {
        // given
        givenOneOutletProvided();
        givenConnectorType(null);

        // expected
        assertThrows(IllegalArgumentException.class, () -> {
            stationFactory.createStation(csvLineMock);
        });
    }

    private void givenConnectorType(String connectorType) {
        doReturn(connectorType).when(csvLineMock).getItem(25);
    }

    private void givenOneOutletProvided() {
        doReturn("1").when(csvLineMock).getItem(3);
    }

    private void givenMaxNumberOfOutletsIsSet() {
        doReturn("1").when(csvLineMock).getItem(4);
    }
}