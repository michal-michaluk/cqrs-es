package devices.configuration.features.catalogue.photo;

import com.jayway.jsonpath.JsonPath;
import devices.configuration.DisableAuthorizationConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;

import static devices.configuration.FileFixture.mockImageWithSize;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StationPhotosController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableAuthorizationConfiguration.class)
class StationPhotosControllerMvcTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    StationPhotoService stationPhotoService;

    @Test
    public void Should_upload_photo_to_station() throws Exception {
        // given
        doReturn(
                PhotoResponse.builder()
                        .id(randomUUID())
                        .category("category")
                        .url(new URL("http://url"))
                        .name("name").build())
                .when(stationPhotoService).store(any(), any(), any());

        // when
        var response = mockMvc.perform(
                multipart("/installation/stations/EVB-1234/photos")
                        .file(mockImageWithSize(5, "photo"))
                        .accept(APPLICATION_JSON)
                        .content("{\"category\":\"ENTRANCE\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // then
        String category = JsonPath.read(response, "$.category");
        assertThat(category).isEqualTo("category");
    }

    @Test
    public void Should_remove_photo_from_station() throws Exception {
        mockMvc.perform(
                delete("/installation/stations/EVB-1234/photos/" + randomUUID())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "any"))
                .andExpect(status().isOk());
    }

    @Test
    public void Should_return_404_for_invalid_photo_UUID() throws Exception {
        var response = mockMvc.perform(
                delete("/installation/stations/EVB-1234/photos/abcd")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        assertThat(response.getErrorMessage()).isEqualTo("Station EVB-1234 has no photo with ID abcd");
    }
}