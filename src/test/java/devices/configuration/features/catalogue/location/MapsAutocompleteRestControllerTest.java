package devices.configuration.features.catalogue.location;

import devices.configuration.DisableAuthorizationConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MapsAutocompleteRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableAuthorizationConfiguration.class)
class MapsAutocompleteRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private GoogleAutocompleteClient autocompletion;

    @Test
    void shouldMapParamsCorrectly() throws Exception {
        // given
        when(autocompletion.locationAutoCompleted("ABC")).thenReturn("OK");

        // when
        String result = mockMvc.perform(get("/locations/autocomplete?input=ABC"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        assertThat(result).isEqualTo("OK");
    }
}