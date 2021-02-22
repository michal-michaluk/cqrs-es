package devices.configuration.features.toggle;

import devices.configuration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class TogglesServiceTest {

    @Autowired
    TogglesService togglesService;

    @Autowired
    TogglesRepository togglesRepository;

    @Test
    void Should_enable_toggle() {
        //given
        String toggleName = "toggle1";
        Toggle toggle = new Toggle();
        toggle.setName(toggleName);
        toggle.setValue(false);
        togglesRepository.save(toggle);

        //when
        togglesService.enableToggle(toggleName);

        //then
        assertThat(togglesService.isEnabled(toggleName, false)).isTrue();
    }
}
