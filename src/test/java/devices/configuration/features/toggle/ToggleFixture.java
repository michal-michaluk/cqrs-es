package devices.configuration.features.toggle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToggleFixture {

    @Autowired
    TogglesRepository togglesRepository;

    private Toggle saveToggle(Toggle toggle) {
        return togglesRepository.save(toggle);
    }

    Toggle someToggleFromDb() {
        return setToggleValue("toggleName", false);
    }

    public void enable(String toggleName) {
        setToggleValue(toggleName, true);
    }

    public Toggle setToggleValue(String toggleName, boolean toggleValue) {
        Toggle toggle = new Toggle();
        toggle.name = toggleName;
        toggle.value = toggleValue;
        return saveToggle(toggle);
    }
}
