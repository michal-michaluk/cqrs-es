package devices.configuration.features.configuration;

import devices.configuration.DisableAuthorizationConfiguration;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeaturesConfigurationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableAuthorizationConfiguration.class)
class FeaturesConfigurationControllerMockMvcTest {

    private static final String MANUFACTURER = "ABB";
    private static final String STATION_TYPE = "Terra 53";
    private static final String POINT_TYPE = "Fast Charger CCS";
    private static final String CPO = "ACS Group";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FeaturesConfigurationProvider configurationProvider;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void Should_return_201_and_list_for_Eggplant_import() throws Exception {
        // given
        when(configurationProvider.getStationImportConfig()).thenReturn(prepareConfig());

        // when
        String result = mockMvc.perform(get("/installation/configuration"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        // then
        assertThat(result).contains(MANUFACTURER);
        assertThat(result).contains(STATION_TYPE);
        assertThat(result).contains(POINT_TYPE);
        assertThat(result).contains(CPO);
    }

    private StationImportConfiguration prepareConfig() {
        List<ChargingStationType> stationTypes = new ArrayList<>();
        stationTypes.add(new ChargingStationType(MANUFACTURER, STATION_TYPE));

        List<String> chargingPointTypes = new ArrayList<>();
        chargingPointTypes.add(POINT_TYPE);

        List<String> cpos = new ArrayList<>();
        cpos.add(CPO);

        StationImportConfiguration config = new StationImportConfiguration(cpos, stationTypes, chargingPointTypes);

        return config;
    }
}
