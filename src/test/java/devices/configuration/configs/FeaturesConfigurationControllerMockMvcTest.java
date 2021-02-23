package devices.configuration.configs;

import devices.configuration.DisableAuthorizationConfiguration;
import devices.configuration.JsonAssert;
import devices.configuration.remote.IntervalRulesFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static devices.configuration.JsonAssert.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeaturesConfigurationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DisableAuthorizationConfiguration.class)
class FeaturesConfigurationControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FeaturesConfigurationRepository repository;

    @Test
    public void existingCurrentRulesOfIntervalRulesConfig() throws Exception {
        // given
        String configName = "IntervalRules";
        when(repository.findByName(configName)).thenReturn(
                Optional.of(FeaturesConfigurationFixture.entity(configName, json(IntervalRulesFixture.currentRules())))
        );

        // when
        String result = mockMvc.perform(get("/configs/{config}", configName))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        JsonAssert.assertThat(result)
                .isExactlyLike(IntervalRulesFixture.currentRules());
    }

    @Test
    public void notExistingConfigName() throws Exception {
        // given
        String configName = "Missing";
        when(repository.findByName(configName)).thenReturn(
                Optional.empty()
        );

        // expected
        mockMvc.perform(get("/configs/{config}", configName))
                .andExpect(status().isNotFound());
    }
}

