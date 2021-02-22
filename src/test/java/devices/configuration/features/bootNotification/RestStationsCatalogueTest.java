package devices.configuration.features.bootNotification;

import devices.configuration.DisableAuthorizationConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static devices.configuration.features.catalogue.StationException.stationNotFound;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BootNotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(DisableAuthorizationConfiguration.class)
class RestStationsCatalogueTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BootNotificationService bootNotificationService;

    @Test
    void Should_return_station_details() throws Exception {
        //given
        StationProtocolDTO stationProtocolDTO = new StationProtocolDTO(
                new ProtocolDTO("ocpp", "2.0", "json"),
                "charging-staion-communication",
                "some station"
        );
        when(bootNotificationService.getStationDetails(anyString())).thenReturn(stationProtocolDTO);

        //when
        mockMvc.perform(
                get("/stations/{stationName}", stationProtocolDTO.getStationName())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.csms").value(stationProtocolDTO.getCsms()))
                .andExpect(jsonPath("$.stationName").value(stationProtocolDTO.getStationName()))
                .andExpect(jsonPath("$.protocol.name").value(stationProtocolDTO.getProtocol().getName()))
                .andExpect(jsonPath("$.protocol.version").value(stationProtocolDTO.getProtocol().getVersion()))
                .andExpect(jsonPath("$.protocol.mediaType").value(stationProtocolDTO.getProtocol().getMediaType()));
    }

    @Test
    void Should_return_not_found_when_cannot_find_station() throws Exception {
        //given
        when(bootNotificationService.getStationDetails(anyString())).thenThrow(stationNotFound("any"));

        //expect
        mockMvc.perform(
                get("/stations/{stationName}", "some-station")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
